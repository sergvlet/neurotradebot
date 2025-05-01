package com.chicu.neurotradebot.strategy;

import com.chicu.neurotradebot.model.AiTradeSettings;
import com.chicu.neurotradebot.model.Candle;
import com.chicu.neurotradebot.model.StrategySignal;
import com.chicu.neurotradebot.repository.TradingStrategy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Getter
@Setter
@NoArgsConstructor
public class RsiEmaStrategy implements TradingStrategy {

    private int emaShort = 9;
    private int emaLong = 21;
    private int rsiPeriod = 14;
    private double rsiBuyThreshold = 30.0;
    private double rsiSellThreshold = 70.0;
    private int minCandles = 50;

    public void resetToDefault() {
        this.emaShort = 9;
        this.emaLong = 21;
        this.rsiPeriod = 14;
        this.rsiBuyThreshold = 30.0;
        this.rsiSellThreshold = 70.0;
        this.minCandles = 50;
    }

    @Override
    public boolean shouldBuy(List<Candle> candles) {
        if (candles.size() < minCandles) return false;
        double emaS = ema(candles, emaShort);
        double emaL = ema(candles, emaLong);
        double rsi = rsi(candles, rsiPeriod);
        return rsi < rsiBuyThreshold && emaS > emaL;
    }

    @Override
    public boolean shouldSell(List<Candle> candles) {
        if (candles.size() < minCandles) return false;
        double emaS = ema(candles, emaShort);
        double emaL = ema(candles, emaLong);
        double rsi = rsi(candles, rsiPeriod);
        return rsi > rsiSellThreshold && emaS < emaL;
    }

    @Override
    public String getName() {
        return "RSI + EMA";
    }

    public StrategySignal evaluate(List<Candle> candles, AiTradeSettings settings) {
        // Применяем параметры из базы
        this.emaShort = settings.getEmaShort();
        this.emaLong = settings.getEmaLong();
        this.rsiPeriod = settings.getRsiPeriod();
        this.rsiBuyThreshold = settings.getRsiBuyThreshold();
        this.rsiSellThreshold = settings.getRsiSellThreshold();
        this.minCandles = settings.getMinCandles();

        if (shouldBuy(candles)) return StrategySignal.BUY;
        if (shouldSell(candles)) return StrategySignal.SELL;
        return StrategySignal.NONE;
    }

    private double ema(List<Candle> candles, int period) {
        double k = 2.0 / (period + 1);
        double ema = candles.get(candles.size() - period).getClose();
        for (int i = candles.size() - period + 1; i < candles.size(); i++) {
            ema = candles.get(i).getClose() * k + ema * (1 - k);
        }
        return ema;
    }

    private double rsi(List<Candle> candles, int period) {
        double gain = 0, loss = 0;
        for (int i = candles.size() - period; i < candles.size(); i++) {
            double change = candles.get(i).getClose() - candles.get(i - 1).getClose();
            if (change > 0) gain += change;
            else loss -= change;
        }
        if (loss == 0) return 100;
        double rs = gain / loss;
        return 100 - (100 / (1 + rs));
    }
}
