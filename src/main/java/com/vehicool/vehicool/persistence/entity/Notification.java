package com.vehicool.vehicool.persistence.entity;

import com.vehicool.vehicool.security.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User corresponingUser;

    @Column(name = "message",nullable = false)
    private String message;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "date_received")
    private Date dateReceived;
}
