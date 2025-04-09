package com.chicu.neurotradebot.trade.model;

import com.chicu.neurotradebot.telegramm.model.UserSettings;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "active_trades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol; // Тикер торговой пары (например, BTCUSDT)
    private String signal; // Сигнал: BUY, SELL
    private BigDecimal quantity; // Количество криптовалюты
    private BigDecimal price; // Цена при открытии
    private LocalDateTime openTime; // Время открытия сделки
    private BigDecimal totalAmount; // Сумма сделки (quantity * price)
    private boolean isDemo; // Флаг, указывающий на режим тестовой торговли

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_settings_id")
    private UserSettings userSettings; // Связь с настройками пользователя (при необходимости)

    public BigDecimal calculateTradeValue() {
        return this.quantity.multiply(this.price);
    }
}
