package com.why.taskmanager.service.impl;

import com.why.taskmanager.entity.Notification;
import com.why.taskmanager.mapper.NotificationMapper;
import com.why.taskmanager.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationMapper.findUnreadByUserId(userId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        notificationMapper.markAsRead(notificationId);
    }

    @Override
    public void createNotification(Long taskId, Long userId, String message) {
        Notification notification = new Notification();
        notification.setTaskId(taskId);
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setCreatedTime(LocalDateTime.now());
        notificationMapper.insert(notification);
    }
}
