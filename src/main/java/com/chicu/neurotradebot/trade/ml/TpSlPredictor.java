package com.chicu.neurotradebot.trade.ml;

import com.chicu.neurotradebot.trade.ml.strategy.IndicatorCalculator.IndicatorValues;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Делает HTTP-POST на ML-сервис и парсит ответ в TpSlResult.
 */
@Component
@RequiredArgsConstructor
public class TpSlPredictor {

    private final ObjectMapper objectMapper;

    // HttpClient с таймаутами
    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    /**
     * Отправляет значения индикаторов на ML-сервис и возвращает TP/SL.
     *
     * @param iv         рассчитанные значения индикаторов
     * @param predictUrl URL вида http://localhost:5000/predict
     */
    public TpSlResult predict(IndicatorValues iv, String predictUrl) {
        try {
            // Собираем JSON-пayload
            JsonNode payload = objectMapper.createObjectNode()
                .put("rsi", iv.getRsi())
                .put("bb_width", iv.getBbWidth())
                .put("atr", iv.getAtr())
                .put("body_ratio", iv.getBodyRatio());

            String body = objectMapper.writeValueAsString(payload);

            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(predictUrl))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                throw new IllegalStateException("HTTP " + resp.statusCode() + ": " + resp.body());
            }

            JsonNode json = objectMapper.readTree(resp.body());
            double tp = json.get("tp").asDouble();
            double sl = json.get("sl").asDouble();

            return new TpSlResult(tp, sl);

        } catch (Exception e) {
            throw new IllegalStateException("Ошибка при запросе ML-сервиса: " + e.getMessage(), e);
        }
    }
}
