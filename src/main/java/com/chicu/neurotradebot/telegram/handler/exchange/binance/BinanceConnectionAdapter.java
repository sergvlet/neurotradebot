package com.chicu.neurotradebot.telegram.handler.exchange.binance;

import com.chicu.neurotradebot.telegram.handler.exchange.common.ExchangeConnectionAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class BinanceConnectionAdapter implements ExchangeConnectionAdapter {

    @Override
    public boolean testConnection(String apiKey, String secretKey, boolean useTestnet) {
        try {
            String baseUrl = useTestnet ? BinanceConstants.TESTNET_BASE_URL : BinanceConstants.MAINNET_BASE_URL;
            long timestamp = System.currentTimeMillis();
            String query = "timestamp=" + timestamp;
            String signature = hmacSHA256(query, secretKey);
            String fullUrl = baseUrl + BinanceConstants.ACCOUNT_ENDPOINT + "?" + query + "&signature=" + signature;

            URL url = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-MBX-APIKEY", apiKey);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            return responseCode == 200;

        } catch (Exception e) {
            log.warn("Binance connection failed: {}", e.getMessage());
            return false;
        }
    }

    private String hmacSHA256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        for (byte b : hmac) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
