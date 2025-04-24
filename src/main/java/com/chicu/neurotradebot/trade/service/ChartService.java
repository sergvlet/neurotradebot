package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.trade.model.UserSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChartService {

    private final MarketCandleService candleService;
    private final UserSettingsService userSettingsService;

    public File generateStrategyChart(Long chatId) {
        UserSettings settings = userSettingsService.getOrCreate(chatId);
        String symbol = String.valueOf(settings.getExchange()); // Пример: BTCUSDT
        String interval = "1h"; // Можно расширить в будущем
        Set<AvailableStrategy> strategies = settings.getStrategies();

        if (symbol == null || strategies == null || strategies.isEmpty()) {
            log.warn("⚠️ Не задан символ или стратегии для пользователя {}", chatId);
            return null;
        }

        return generateChart(symbol, interval, strategies);
    }

    public File generateChart(String symbol, String interval, Set<AvailableStrategy> strategies) {
        List<MarketCandle> candles = candleService.getCandles(symbol, interval);
        if (candles.isEmpty()) {
            log.warn("📉 Нет данных свечей для {}", symbol);
            return null;
        }

        BarSeries series = candleService.buildBarSeries(candles);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        List<Date> xData = candles.stream()
                .map(c -> Date.from(c.getTimestamp()))  // Используем openTime для преобразования в Date
                .collect(Collectors.toList());

        List<Double> closeData = candles.stream()
                .map(MarketCandle::getClose)
                .collect(Collectors.toList());

        XYChart chart = new XYChartBuilder()
                .width(1000)
                .height(600)
                .title("📊 График: " + strategies.stream().map(AvailableStrategy::getTitle).collect(Collectors.joining(", ")))
                .xAxisTitle("Время")
                .yAxisTitle("Цена (USDT)")
                .build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        chart.getStyler().setMarkerSize(4);

        chart.addSeries("Цена", xData, closeData).setLineColor(Color.BLACK);

        if (strategies.contains(AvailableStrategy.SMA)) {
            SMAIndicator sma5 = new SMAIndicator(closePrice, 5);
            SMAIndicator sma20 = new SMAIndicator(closePrice, 20);
            chart.addSeries("SMA(5)", xData, toDoubleList(sma5)).setLineColor(Color.BLUE);
            chart.addSeries("SMA(20)", xData, toDoubleList(sma20)).setLineColor(Color.CYAN);
        }

        if (strategies.contains(AvailableStrategy.EMA)) {
            EMAIndicator ema10 = new EMAIndicator(closePrice, 10);
            chart.addSeries("EMA(10)", xData, toDoubleList(ema10)).setLineColor(Color.ORANGE);
        }

        try {
            File file = new File("strategy_chart_" + System.currentTimeMillis() + ".png");
            BitmapEncoder.saveBitmap(chart, file.getAbsolutePath(), BitmapEncoder.BitmapFormat.PNG);
            return file;
        } catch (Exception e) {
            log.error("❌ Ошибка при генерации графика стратегии", e);
            return null;
        }
    }

    private List<Double> toDoubleList(org.ta4j.core.Indicator<org.ta4j.core.num.Num> indicator) {
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < indicator.getBarSeries().getBarCount(); i++) {
            values.add(indicator.getValue(i).doubleValue());
        }
        return values;
    }
}
