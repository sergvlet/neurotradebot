package com.chicu.neurotradebot.audit.entity;

import com.chicu.neurotradebot.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "session_logs")
public class SessionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private SessionEventType eventType;

    private String ipAddress;

    private String userAgent;

    private LocalDateTime createdAt;

    public enum SessionEventType {
        LOGIN,
        LOGOUT,
        ERROR
    }
}
