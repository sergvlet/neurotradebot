package com.chicu.neurotradebot.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_trading_settings")
@Getter
@Setter
@NoArgsConstructor
public class UserTradingSettings {

    @Id
    private Long userId; // Telegram ID (Primary Key)

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user; // Прямая связь с User

    private String exchange; // Выбранная биржа

    private Boolean useTestnet; // true = тестовая сеть, false = реальная

    private String tradingMode; // MANUAL или AI
}
