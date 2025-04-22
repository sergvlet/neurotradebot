package com.chicu.neurotradebot.trade.model;

import com.chicu.neurotradebot.trade.enums.Exchange;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_api_keys")
public class UserApiKeys {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;

    @Enumerated(EnumType.STRING)
    private Exchange exchange;

    // Реальные ключи
    private String realApiKey;
    private String realApiSecret;

    // Тестовые ключи
    private String testApiKey;
    private String testApiSecret;

    public boolean hasRealKeys() {
        return realApiKey != null && realApiSecret != null;
    }

    public boolean hasTestKeys() {
        return testApiKey != null && testApiSecret != null;
    }
}
