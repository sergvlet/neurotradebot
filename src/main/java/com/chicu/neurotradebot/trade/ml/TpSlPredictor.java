package com.chicu.neurotradebot.trade.ml;

import com.chicu.neurotradebot.trade.ml.strategy.IndicatorCalculator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Компонент для вызова ML REST-сервиса и получения TP/SL.
 */
@Component
@RequiredArgsConstructor
public class TpSlPredictor {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    /**
     * Отправить в ML-сервис рассчитанные индикаторы и получить TP/SL.
     *
     * @param iv  значения индикаторов
     * @param url адрес эндпоинта (из MlStrategyConfig.getPredictUrl())
     * @return    tp/sl в процентах
     */
    public TpSlResult predict(IndicatorCalculator.IndicatorValues iv, String url) {
        try {
            String body = objectMapper.writeValueAsString(iv);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                throw new RuntimeException("ML-сервис вернул HTTP " + resp.statusCode());
            }
            var node = objectMapper.readTree(resp.body());
            double tp = node.get("tp").asDouble();
            double sl = node.get("sl").asDouble();
            return new TpSlResult(tp, sl);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при запросе к ML-сервису", e);
        }
    }
}
