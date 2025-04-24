package com.chicu.neurotradebot.ai.strategy.impl;

import com.chicu.neurotradebot.ai.strategy.AiStrategy;
import com.chicu.neurotradebot.ai.strategy.config.BollingerBandsConfig;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.enums.Signal;
import com.chicu.neurotradebot.trade.service.MarketCandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BollingerBandsStrategy implements AiStrategy {

    private final MarketCandleService candleService;
    private BollingerBandsConfig config = new BollingerBandsConfig(); // по умолчанию

    @Override
    public String getName() {
        return "Bollinger Bands";
    }

    @Override
    public Signal analyze(List<MarketCandle> candles) {
        if (candles.size() < config.getPeriod()) {
            log.warn("📉 Недостаточно данных для Bollinger Bands (необходим период: {})", config.getPeriod());
            return Signal.HOLD;  // Если данных недостаточно для анализа, возвращаем HOLD
        }

        // Строим серию баров для анализа
        BarSeries series = candleService.buildBarSeries(candles);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        SMAIndicator sma = new SMAIndicator(closePrice, config.getPeriod());
        StandardDeviationIndicator stdDev = new StandardDeviationIndicator(closePrice, config.getPeriod());

        // Средняя линия, верхняя и нижняя полосы Bollinger Bands
        BollingerBandsMiddleIndicator middleBand = new BollingerBandsMiddleIndicator(sma);
        Num multiplier = series.numOf(config.getMultiplier());
        BollingerBandsUpperIndicator upperBand = new BollingerBandsUpperIndicator(middleBand, stdDev, multiplier);
        BollingerBandsLowerIndicator lowerBand = new BollingerBandsLowerIndicator(middleBand, stdDev, multiplier);

        // Индекс последней свечи
        int lastIndex = series.getEndIndex();
        Num close = closePrice.getValue(lastIndex);
        Num lower = lowerBand.getValue(lastIndex);
        Num upper = upperBand.getValue(lastIndex);

        log.info("📊 Bollinger Bands: close={}, upper={}, lower={}", close, upper, lower);

        // Логика принятия решения: покупка, продажа или удержание
        if (close.isLessThan(lower)) {
            return Signal.BUY;  // Цена ниже нижней полосы — сигнал на покупку
        } else if (close.isGreaterThan(upper)) {
            return Signal.SELL;  // Цена выше верхней полосы — сигнал на продажу
        } else {
            return Signal.HOLD;  // Цена внутри полос — удержание позиции
        }
    }

    @Override
    public void setConfig(Object config) {
        if (config instanceof BollingerBandsConfig bollingerConfig) {
            this.config = bollingerConfig;
        } else {
            log.warn("❌ Неверная конфигурация передана в BollingerBandsStrategy: {}", config);
        }
    }

    @Override
    public void execute() {
        // Выполнение стратегии: анализируем последние данные и принимаем решение
        log.info("Запуск стратегии Bollinger Bands...");

        // Получаем последние данные для анализа (например, 100 свечей)
        List<MarketCandle> latestCandles = candleService.getLatestCandles("BTCUSDT", "1h", 100);

        // Выполняем анализ и получаем решение
        Signal decision = analyze(latestCandles);

        // Реализуем логику для исполнения ордера в зависимости от сигнала
        if (decision == Signal.BUY) {
            log.info("💡 Сигнал на покупку. Размещение ордера...");
            // Здесь мы можем отправить ордер на покупку
            // Например, в OrderService можно разместить ордер
            // orderService.placeBuyOrder(chatId, "BTCUSDT", qty, true);
            // Уведомляем пользователя
            // notificationService.sendTradeNotification(chatId, Signal.BUY);
        } else if (decision == Signal.SELL) {
            log.info("💡 Сигнал на продажу. Размещение ордера...");
            // Отправляем ордер на продажу
            // orderService.placeSellOrder(chatId, "BTCUSDT", qty, true);
            // Уведомляем пользователя
            // notificationService.sendTradeNotification(chatId, Signal.SELL);
        } else {
            log.info("⚪️ Сигнал на удержание. Сделка не размещена.");
        }
    }
}
