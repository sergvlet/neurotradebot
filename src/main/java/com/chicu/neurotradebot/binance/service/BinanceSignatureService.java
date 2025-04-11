package com.chicu.neurotradebot.binance.service;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


@Service
public class BinanceSignatureService {

    private static final String HMAC_SHA256 = "HmacSHA256";

    public String sign(String queryParams, String secretKey) {
        try {
            Mac sha256_HMAC = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), HMAC_SHA256);
            sha256_HMAC.init(secret_key);
            byte[] signedData = sha256_HMAC.doFinal(queryParams.getBytes());
            return Base64.getEncoder().encodeToString(signedData);  // Возвращаем строку с подписью
        } catch (Exception e) {
            throw new RuntimeException("Error signing the query string", e);
        }
    }
}