package com.chicu.neurotradebot.binance;

import com.chicu.neurotradebot.trade.model.MarketCandle;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLSocketFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Instant;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

@Slf4j
public class BinanceKlineWebSocketClient implements WebSocket.Listener {

    private final String symbol;
    private final String interval;
    private final Consumer<MarketCandle> onCandle;

    private WebSocket webSocket;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public BinanceKlineWebSocketClient(String symbol, String interval, Consumer<MarketCandle> onCandle) {
        this.symbol = symbol.toLowerCase();
        this.interval = interval;
        this.onCandle = onCandle;
    }

    public void connect() {
        String url = String.format("wss://stream.binance.com:9443/ws/%s@kline_%s", symbol, interval);

        HttpClient client = HttpClient.newHttpClient();

        client.newWebSocketBuilder()
                .buildAsync(URI.create(url), this)
                .thenAccept(ws -> this.webSocket = ws);

        log.info("📡 WebSocket подключение к Binance: {}", url);
    }


    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        webSocket.request(1);
        try {
            JsonNode node = objectMapper.readTree(data.toString());
            JsonNode kline = node.get("k");

            if (kline != null && kline.get("x").asBoolean()) { // закрытая свеча
                MarketCandle candle = MarketCandle.builder()
                        .timestamp(Instant.ofEpochMilli(kline.get("t").asLong()))
                        .open(Double.parseDouble(kline.get("o").asText()))
                        .high(Double.parseDouble(kline.get("h").asText()))
                        .low(Double.parseDouble(kline.get("l").asText()))
                        .close(Double.parseDouble(kline.get("c").asText()))
                        .volume(Double.parseDouble(kline.get("v").asText()))
                        .closeTime(Instant.ofEpochMilli(kline.get("T").asLong()))
                        .build();

                onCandle.accept(candle);
            }

        } catch (Exception e) {
            log.error("❗ Ошибка обработки свечи Binance", e);
        }
        return null; // ИЛИ: return CompletableFuture.completedFuture(null);
    }

    public void close() {
        if (webSocket != null) {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Closing").thenRun(() ->
                    log.info("🛑 WebSocket закрыт для {}", symbol));
        }
    }

    @Override public void onOpen(WebSocket webSocket) { webSocket.request(1); }
    @Override public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) { return null; }
    @Override public void onError(WebSocket webSocket, Throwable error) { log.error("WebSocket ошибка", error); }
}
