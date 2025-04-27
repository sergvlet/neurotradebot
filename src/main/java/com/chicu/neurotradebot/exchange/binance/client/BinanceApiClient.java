package com.chicu.neurotradebot.exchange.binance.client;

import com.chicu.neurotradebot.exchange.core.ExchangeClient;
import com.chicu.neurotradebot.user.service.ExchangeCredentialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceApiClient implements ExchangeClient {

    private final WebClient webClient = WebClient.create();
    private final ExchangeCredentialService exchangeCredentialService;

    private String sign(String data, String secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : signature) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при подписании запроса Binance", e);
        }
    }

    @Override
    public BigDecimal getBalance(String asset, Long userId) {
        boolean useTestnet = exchangeCredentialService.isTestnetEnabled(userId);
        String apiKey = exchangeCredentialService.getApiKey(userId, useTestnet);
        String secretKey = exchangeCredentialService.getSecretKey(userId, useTestnet);
        String baseUrl = exchangeCredentialService.getBaseUrl(userId, useTestnet);

        String query = "timestamp=" + Instant.now().toEpochMilli();
        String signature = sign(query, secretKey);

        log.debug("Отправляем запрос на Binance для баланса: {}?{}&signature=***", baseUrl + "/api/v3/account", query);

        Map response = webClient.get()
                .uri(URI.create(baseUrl + "/api/v3/account?" + query + "&signature=" + signature))
                .header("X-MBX-APIKEY", apiKey)
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(resp -> log.debug("Ответ от Binance (getBalance): {}", resp))
                .block();

        if (response == null || !response.containsKey("balances")) {
            throw new RuntimeException("Ошибка получения баланса");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> balances = (List<Map<String, Object>>) response.get("balances");

        var assetBalance = balances.stream()
                .filter(b -> asset.equalsIgnoreCase((String) b.get("asset")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Актив " + asset + " не найден"));

        return new BigDecimal((String) assetBalance.get("free"));
    }

    @Override
    public String placeBuyOrder(String symbol, BigDecimal quantity, Long userId) {
        return placeOrder(symbol, quantity, "BUY", userId);
    }

    @Override
    public String placeSellOrder(String symbol, BigDecimal quantity, Long userId) {
        return placeOrder(symbol, quantity, "SELL", userId);
    }

    private String placeOrder(String symbol, BigDecimal quantity, String side, Long userId) {
        boolean useTestnet = exchangeCredentialService.isTestnetEnabled(userId);
        String apiKey = exchangeCredentialService.getApiKey(userId, useTestnet);
        String secretKey = exchangeCredentialService.getSecretKey(userId, useTestnet);
        String baseUrl = exchangeCredentialService.getBaseUrl(userId, useTestnet);

        Map<String, String> params = Map.of(
                "symbol", symbol,
                "side", side,
                "type", "MARKET",
                "quantity", quantity.toPlainString(),
                "timestamp", String.valueOf(Instant.now().toEpochMilli())
        );

        String queryString = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        String signature = sign(queryString, secretKey);

        log.debug("Отправляем ордер на Binance: side={}, symbol={}, quantity={}", side, symbol, quantity);

        return webClient.post()
                .uri(URI.create(baseUrl + "/api/v3/order?" + queryString + "&signature=" + signature))
                .header("X-MBX-APIKEY", apiKey)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(resp -> log.debug("Ответ от Binance (placeOrder): {}", resp))
                .block();
    }

    @Override
    public boolean testConnection(Long userId) {
        try {
            getBalance("USDT", userId);
            log.debug("Тест подключения к Binance успешен для userId={}", userId);
            return true;
        } catch (Exception e) {
            log.error("Ошибка теста подключения к Binance для userId={}: {}", userId, e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, BigDecimal> getAllBalances(Long userId) {
        boolean useTestnet = exchangeCredentialService.isTestnetEnabled(userId);
        String apiKey = exchangeCredentialService.getApiKey(userId, useTestnet);
        String secretKey = exchangeCredentialService.getSecretKey(userId, useTestnet);
        String baseUrl = exchangeCredentialService.getBaseUrl(userId, useTestnet);

        String query = "timestamp=" + Instant.now().toEpochMilli();
        String signature = sign(query, secretKey);

        log.debug("Отправляем запрос на Binance для всех балансов: {}?{}&signature=***", baseUrl + "/api/v3/account", query);

        Map response = webClient.get()
                .uri(URI.create(baseUrl + "/api/v3/account?" + query + "&signature=" + signature))
                .header("X-MBX-APIKEY", apiKey)
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(resp -> log.debug("Ответ от Binance (getAllBalances): {}", resp))
                .block();

        if (response == null) {
            throw new RuntimeException("Ошибка получения списка балансов: пустой ответ");
        }

        if (response.containsKey("code")) {
            throw new RuntimeException("Ошибка Binance API: " + response.get("msg"));
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> balances = (List<Map<String, Object>>) response.get("balances");

        return balances.stream()
                .filter(b -> new BigDecimal((String) b.get("free")).compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toMap(
                        b -> (String) b.get("asset"),
                        b -> new BigDecimal((String) b.get("free"))
                ));
    }
}
