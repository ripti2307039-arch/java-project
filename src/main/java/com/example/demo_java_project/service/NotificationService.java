package com.example.demo_java_project.service;

import com.example.demo_java_project.dao.NotificationDAO;
import com.example.demo_java_project.model.Notification;

import java.util.List;

public class NotificationService {

    private final NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
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
}