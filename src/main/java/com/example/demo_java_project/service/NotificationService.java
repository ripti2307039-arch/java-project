package com.example.demo_java_project.service;

import com.example.demo_java_project.api.ApiClient;
import com.example.demo_java_project.api.ExternalPost;
import com.example.demo_java_project.api.JsonService;
import com.example.demo_java_project.dao.NotificationDAO;
import com.example.demo_java_project.model.Notification;

import java.util.List;

public class NotificationService {

    private final NotificationDAO notificationDAO;
    private final ApiClient apiClient;
    private final JsonService jsonService;

    private static final String EXTERNAL_API_URL = "https://jsonplaceholder.typicode.com/posts";

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
            String json = apiClient.get(EXTERNAL_API_URL);
            List<ExternalPost> posts = jsonService.parsePostList(json);

            int savedCount = 0;
            int limit = Math.min(howMany, posts.size());

            for (int i = 0; i < limit; i++) {
                ExternalPost post = posts.get(i);
                String message = post.getTitle();
                boolean saved = sendNotification(userId, message);
                if (saved) {
                    savedCount++;
                }
            }

            return savedCount;

        } catch (Exception e) {
            System.err.println("Failed to fetch external notifications: " + e.getMessage());
            return 0;
        }
    }
}