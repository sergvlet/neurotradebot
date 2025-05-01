package com.chicu.neurotradebot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Candle {

    private ZonedDateTime time;         // Время открытия свечи
    private double open;                // Цена открытия
    private double high;                // Максимум
    private double low;                 // Минимум
    private double close;               // Цена закрытия
    private double volume;              // Объём в базовой валюте

    private double quoteVolume;         // Объём в котируемой валюте (например USDT)
    private long tradeCount;            // Кол-во сделок за свечу
    private double takerBuyBaseVolume;  // Объём рыночных покупок (в базовой валюте)
    private double takerBuyQuoteVolume; // Объём рыночных покупок (в котируемой валюте)

    // Вспомогательные методы, если нужно
    public double getTypicalPrice() {
        return (high + low + close) / 3;
    }

    public double getCandleRange() {
        return high - low;
    }

    public boolean isBullish() {
        return close > open;
    }

    public boolean isBearish() {
        return close < open;
    }
}
