package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.chicu.neurotradebot.binance.BinanceKlineWebSocketClient;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // Импортируем аннотацию Slf4j
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j  // Аннотация для добавления log
@Service
@RequiredArgsConstructor
public class MarketStreamService {

    // Хранение активных WebSocket-подключений
    private final Map<String, BinanceKlineWebSocketClient> activeStreams = new ConcurrentHashMap<>();

    /**
     * Подписка на WebSocket-канал для свечей.
     *
     * @param symbol  торговая пара (например, BTCUSDT)
     * @param interval таймфрейм (например, 1m, 5m)
     * @param onCandle обработчик данных свечи
     */
    public void subscribeToCandles(String symbol, String interval, Consumer<MarketCandle> onCandle) {
        // Формирование ключа потока
        String streamKey = symbol + "@" + interval;

        // Проверка, не подписан ли уже
        if (activeStreams.containsKey(streamKey)) {
            log.info("Уже подписан на поток: {}", streamKey);
            return;
        }

        // Создание WebSocket-клиента для получения данных
        BinanceKlineWebSocketClient client = new BinanceKlineWebSocketClient(symbol, interval, onCandle);
        client.connect();

        // Добавление клиента в карту активных потоков
        activeStreams.put(streamKey, client);

        log.info("✅ Подписка на WebSocket для {} запущена", streamKey);
    }

    /**
     * Закрытие всех WebSocket-соединений перед завершением работы приложения
     */
    @PreDestroy
    public void stopAll() {
        activeStreams.values().forEach(BinanceKlineWebSocketClient::close);
        log.info("Все WebSocket соединения закрыты.");
    }
}
