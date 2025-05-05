package com.chicu.neurotradebot.entity;

import com.chicu.neurotradebot.enums.ApiSetupStep;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "ai_trade_settings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AiTradeSettings {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ссылка на владельца
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    /** Биржа (например "binance", "ftx" и т.д.) */
    @Column(length = 32)
    private String exchange;

    /** true — TESTNET, false — REAL */
    @Column(name = "test_mode", nullable = false)
    private boolean testMode;

    /** Текущий шаг настройки ключей */
    @Enumerated(EnumType.STRING)
    @Column(name = "api_setup_step", nullable = false)
    private ApiSetupStep apiSetupStep = ApiSetupStep.NONE;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // один AiTradeSettings — много ApiCredentials
    @OneToMany(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApiCredentials> credentials;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = Instant.now();
        apiSetupStep = ApiSetupStep.NONE;
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
