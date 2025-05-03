// src/main/java/com/chicu/neurotradebot/entity/AiTradeSettings.java
package com.chicu.neurotradebot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "ai_trade_settings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AiTradeSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   id;

    /** Связь с пользователем */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "exchange", length = 32)
    private String exchange;

    @Column(name = "ai_enabled", nullable = false)
    private boolean aiEnabled;

    @Column(name = "test_mode", nullable = false)
    private boolean testMode;

    @Column(name = "scan_interval_seconds", nullable = false)
    private int     scanIntervalSeconds;

    @Column(name = "selected_pair", length = 32)
    private String  selectedPair;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist protected void onCreate() {
        createdAt          = Instant.now();
        updatedAt          = Instant.now();
        aiEnabled          = false;
        testMode           = false;
        scanIntervalSeconds = 60;
    }
    @PreUpdate  protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
