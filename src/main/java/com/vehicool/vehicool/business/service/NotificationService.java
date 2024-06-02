package com.vehicool.vehicool.business.service;

import com.vehicool.vehicool.persistence.entity.Notification;
import com.vehicool.vehicool.persistence.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }
    public Notification getById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }
    public Notification update(Notification notification,Long id) {
        notification.setId(id);
        return notificationRepository.saveAndFlush(notification);
    }

    public void delete(Notification notification) {
        notificationRepository.delete(notification);
    }

    public List<Notification> findUserNotifications(String username){
        return notificationRepository.findUserNotifications(username);
    }
    public Long findUnreadNotificationCounter(String username){
        return notificationRepository.newNotificationCounter(username);
    }
}
