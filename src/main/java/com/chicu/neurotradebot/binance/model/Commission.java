package com.chicu.neurotradebot.binance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Commission {

    private String symbol; // Символ актива (например, "BTCUSDT")
    private BigDecimal makerCommission; // Процент комиссии для создателей ордеров (Maker)
    private BigDecimal takerCommission; // Процент комиссии для тех, кто исполняет ордер (Taker)

    // Вычисление комиссии на основе цены и количества актива
    public BigDecimal calculateMakerCommission(BigDecimal price, BigDecimal quantity) {
        return price.multiply(quantity).multiply(makerCommission).divide(BigDecimal.valueOf(100));
    }

    public BigDecimal calculateTakerCommission(BigDecimal price, BigDecimal quantity) {
        return price.multiply(quantity).multiply(takerCommission).divide(BigDecimal.valueOf(100));
    }
}
