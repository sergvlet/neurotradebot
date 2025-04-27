package com.chicu.neurotradebot.trading.entity;

import com.chicu.neurotradebot.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "trading_sessions")
public class TradingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String exchange;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @Enumerated(EnumType.STRING)
    private SessionMode mode;

    private LocalDateTime startedAt;

    private LocalDateTime stoppedAt;

    public enum SessionStatus {
        ACTIVE,
        STOPPED
    }

    public enum SessionMode {
        MANUAL,
        AI
    }
}
