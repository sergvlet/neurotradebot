package com.chicu.neurotradebot.binance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Type type; // Тип ордера, например, LIMIT, MARKET и т.д.

    // Перечисление типов ордеров
    public enum Type {
        LIMIT,            // Стандартный ордер с лимитом
        MARKET,           // Рыночный ордер
        STOP_LIMIT,       // Стоп-лимит ордер
        STOP_MARKET,      // Стоп-рыночный ордер
        LIMIT_MAKER,      // Лимитный ордер только для Maker
        FOK,              // Fill-or-Kill ордер
        IOC,              // Immediate-or-Cancel ордер
        OCO               // One-Cancels-Other ордер
    }


}
