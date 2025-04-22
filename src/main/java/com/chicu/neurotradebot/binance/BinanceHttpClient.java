package com.chicu.neurotradebot.binance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.io.InputStream;
import java.util.Scanner;

@Slf4j
@Component
public class BinanceHttpClient {

    public String sendSignedRequest(String baseUrl, String path, String method, String apiKey, String secretKey, Map<String, String> params) {
        try {
            long timestamp = System.currentTimeMillis();
            params.put("timestamp", String.valueOf(timestamp));

            String queryString = getQueryString(params);
            String signature = hmacSHA256(queryString, secretKey);
            String fullUrl = baseUrl + path + "?" + queryString + "&signature=" + signature;

            URL url = new URL(fullUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("X-MBX-APIKEY", apiKey);

            InputStream inputStream = conn.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            StringBuilder result = new StringBuilder();
            while (scanner.hasNext()) {
                result.append(scanner.nextLine());
            }
            scanner.close();

            return result.toString();

        } catch (Exception e) {
            log.error("❌ Ошибка при выполнении запроса в Binance API", e);
            return null;
        }
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKeySpec);
        byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hash = new StringBuilder();
        for (byte b : signedBytes) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }

    private String getQueryString(Map<String, String> params) {
        return params.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" +
                          URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }
}
