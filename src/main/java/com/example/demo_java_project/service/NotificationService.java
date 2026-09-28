package com.example.demo_java_project.service;

import com.example.demo_java_project.api.ApiClient;
import com.example.demo_java_project.api.JsonService;
import com.example.demo_java_project.api.PublicHoliday;
import com.example.demo_java_project.dao.NotificationDAO;
import com.example.demo_java_project.model.Notification;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class NotificationService {

    private final NotificationDAO notificationDAO;
    private final ApiClient apiClient;
    private final JsonService jsonService;

    private static final String COUNTRY_CODE = "BD";
    private static final String HOLIDAY_API_BASE_URL = "https://date.nager.at/api/v3/PublicHolidays/";

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
        this.apiClient = new ApiClient();
        this.jsonService = new JsonService();
    }

    public List<Notification> getNotificationsForUser(int userId) {
        return notificationDAO.findByUserId(userId);
    }

    public boolean sendNotification(int userId, String message) {
        Notification notification = new Notification(userId, message);
        int newId = notificationDAO.createNotification(notification);
        return newId != -1;
    }

    public boolean markAsRead(int notificationId) {
        return notificationDAO.markAsRead(notificationId);
    }

    public boolean deleteNotification(int id) {
        return notificationDAO.deleteNotification(id);
    }

    public int fetchExternalNotifications(int userId, int howMany) {
        try {
            LocalDate today = LocalDate.now();

            List<PublicHoliday> holidays = new ArrayList<>(fetchHolidays(today.getYear()));
            try {
                holidays.addAll(fetchHolidays(today.getYear() + 1));
            } catch (Exception e) {
                System.err.println("Next year's holidays not available: " + e.getMessage());
            }

            Set<String> existingMessages = new HashSet<>();
            for (Notification existing : notificationDAO.findByUserId(userId)) {
                existingMessages.add(existing.getMessage());
            }

            int savedCount = 0;
            int considered = 0;

            for (PublicHoliday holiday : holidays) {
                if (considered >= howMany) {
                    break;
                }

                LocalDate date = LocalDate.parse(holiday.getDate());
                if (date.isBefore(today)) {
                    continue;
                }
                considered++;

                String message = buildMessage(holiday, date);
                if (existingMessages.contains(message)) {
                    continue;
                }

                if (sendNotification(userId, message)) {
                    savedCount++;
                    existingMessages.add(message);
                }
            }

            return savedCount;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch holiday notifications: " + e.getMessage(), e);
        }
    }

    private List<PublicHoliday> fetchHolidays(int year) throws IOException, InterruptedException {
        String json = apiClient.get(HOLIDAY_API_BASE_URL + year + "/" + COUNTRY_CODE);
        return jsonService.parseHolidayList(json);
    }

    private String buildMessage(PublicHoliday holiday, LocalDate date) {
        String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return "Upcoming public holiday: " + holiday.getName() + " on " + date + " (" + dayName + ")";
    }
}