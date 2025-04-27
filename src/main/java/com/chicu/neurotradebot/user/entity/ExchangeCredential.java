package com.chicu.neurotradebot.user.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "exchange_credentials")
public class ExchangeCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String exchange; // Например "BINANCE"

    private String realApiKey;
    private String realSecretKey;

    private String testApiKey;
    private String testSecretKey;

    private boolean useTestnet;

    private LocalDateTime createdAt;

    public String getApiKey() {
        return useTestnet ? testApiKey : realApiKey;
    }

    public String getSecretKey() {
        return useTestnet ? testSecretKey : realSecretKey;
    }
}
