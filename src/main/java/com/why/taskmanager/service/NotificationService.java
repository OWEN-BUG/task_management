package com.why.taskmanager.service;

import com.why.taskmanager.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getUnreadNotifications(Long userId);
    void markAsRead(Long notificationId);
    void createNotification(Long taskId, Long userId, String message);
}
