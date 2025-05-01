package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.model.AiTradeSettings;
import com.chicu.neurotradebot.model.Candle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiVisualizationService {

    private final BinanceCandleService binanceCandleService;

    public byte[] generateChart(AiTradeSettings settings) {
        String pair = settings.getManualPair();
        boolean isTestnet = Boolean.TRUE.equals(settings.getUserTradingSettings().getUseTestnet());

        List<Candle> candles = binanceCandleService.getRecentCandles(pair, "15m", 100, isTestnet);
        if (candles.size() < 20) {
            throw new IllegalStateException("Недостаточно данных для визуализации");
        }

        TimeSeries priceSeries = new TimeSeries("Цена");
        TimeSeries emaSeries = new TimeSeries("EMA " + settings.getEmaShort());
        TimeSeries rsiSeries = new TimeSeries("RSI");

        double k = 2.0 / (settings.getEmaShort() + 1.0);
        double ema = candles.get(0).getClose();

        for (int i = 0; i < candles.size(); i++) {
            Candle candle = candles.get(i);
            Second second = new Second(java.util.Date.from(candle.getTime().toInstant()));


            double close = candle.getClose();
            ema = close * k + ema * (1 - k);
            priceSeries.add(second, close);
            emaSeries.add(second, ema);

            if (i >= settings.getRsiPeriod()) {
                double rsi = calculateRSI(candles, i, settings.getRsiPeriod());
                rsiSeries.add(second, rsi);
            }
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(priceSeries);
        dataset.addSeries(emaSeries);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "График AI: " + pair,
                "Время",
                "Цена",
                dataset,
                true,
                true,
                false
        );

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BufferedImage image = chart.createBufferedImage(1200, 1000);
            ChartUtils.writeBufferedImageAsPNG(out, image);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Ошибка при создании графика", e);
            throw new RuntimeException("Ошибка генерации графика", e);
        }
    }

    private double calculateRSI(List<Candle> candles, int index, int period) {
        double gain = 0, loss = 0;
        for (int i = index - period + 1; i <= index; i++) {
            double change = candles.get(i).getClose() - candles.get(i - 1).getClose();
            if (change > 0) gain += change;
            else loss -= change;
        }
        if (loss == 0) return 100.0;
        double rs = gain / loss;
        return 100.0 - (100.0 / (1.0 + rs));
    }
}
