package com.chicu.neurotradebot.telegram.handler.exchange.entity;

import com.chicu.neurotradebot.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "exchange_credentials")
public class ExchangeCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // <-- теперь здесь ссылка на пользователя

    private String exchange;

    private Boolean useTestnet;

    private String realApiKey;
    private String realSecretKey;

    private String testApiKey;
    private String testSecretKey;

    private LocalDateTime createdAt;
}
