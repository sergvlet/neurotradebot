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

    // владелец
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

    // связи с ключами
    @OneToMany(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApiCredentials> credentials;

    /** ID последнего бот-сообщения с подсказкой во время ввода ключей */
    @Column(name = "api_setup_prompt_msg_id", nullable = true)
    private Integer apiSetupPromptMsgId;

    @PrePersist
    protected void onCreate() {
        createdAt           = Instant.now();
        updatedAt           = createdAt;
        apiSetupStep        = ApiSetupStep.NONE;
        apiSetupPromptMsgId = null;
    }

    @PreUpdate
    protected void onUpdate() {
        // при любом обновлении меняем только метку времени
        updatedAt = Instant.now();
        // apiSetupStep и apiSetupPromptMsgId сохраняются до тех пор,
        // пока вы их не сбросите вручную в логике приложения
    }
}
