package com.chicu.neurotradebot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "api_credentials")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApiCredentials {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // владелец — пользователь
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // относится к одной конфигурации (биржа+режим)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "settings_id", nullable = false)
    private AiTradeSettings settings;

    /** Метка ключа, например "main" или "testnet" */
    @Column(nullable = false, length = 50)
    private String label;

    @Column(name = "api_key", nullable = false)
    private String apiKey;

    @Column(name = "api_secret", nullable = false)
    private String apiSecret;

    /** Отмечает, какой ключ сейчас используется */
    @Column(nullable = false)
    private boolean active;
}
