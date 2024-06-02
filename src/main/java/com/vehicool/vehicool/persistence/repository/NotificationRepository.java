package com.vehicool.vehicool.persistence.repository;

import com.vehicool.vehicool.persistence.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    @Query("SELECT count (nt) from Notification nt where nt.corresponingUser.username = :username and nt.isRead = false ")
    public Long newNotificationCounter(String username);

    @Query("SELECT nt from Notification nt where nt.corresponingUser.username = :username")
    public List<Notification> findUserNotifications(String username);
}
