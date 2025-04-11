package com.chicu.neurotradebot.trade.model;

import com.chicu.neurotradebot.telegramm.model.UserSettings;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "closed_trades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClosedTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol; // Тикер торговой пары (например, BTCUSDT)
    private String signal; // Сигнал для закрытия сделки: BUY, SELL
    private BigDecimal quantity; // Количество криптовалюты
    private BigDecimal openPrice; // Цена при открытии сделки
    private BigDecimal closePrice; // Цена при закрытии сделки
    private BigDecimal totalAmount; // Сумма сделки (quantity * price)
    private BigDecimal profitLoss; // Прибыль или убыток от сделки
    private LocalDateTime openTime; // Время открытия сделки
    private LocalDateTime closeTime; // Время закрытия сделки
    private boolean isDemo; // Флаг, указывающий на режим тестовой торговли

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_settings_id")
    private UserSettings userSettings; // Связь с настройками пользователя (при необходимости)

    public BigDecimal calculateProfitLoss() {
        return this.quantity.multiply(this.closePrice.subtract(this.openPrice));
    }
}
