package com.chicu.neurotradebot.binance;

import com.chicu.neurotradebot.binance.service.BinanceSignatureService;
import com.chicu.neurotradebot.config.BinanceProperties;
import com.chicu.neurotradebot.telegramm.model.User;
import com.chicu.neurotradebot.telegramm.service.UserService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class BinanceHttpClient {

    private final OkHttpClient client;
    private final String baseUrl;
    private final String testBaseUrl;
    private final UserService userService;  // Сервис для получения данных пользователя
    private final BinanceSignatureService binanceSignatureService;  // Сервис для подписи запросов

    // Конструктор для настройки клиента и URL API
    @Autowired
    public BinanceHttpClient(BinanceProperties binanceProperties, UserService userService, BinanceSignatureService binanceSignatureService) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // Настроить таймауты
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.baseUrl = binanceProperties.getBaseUrl();
        this.testBaseUrl = binanceProperties.getTestBaseUrl();
        this.userService = userService;
        this.binanceSignatureService = binanceSignatureService; // Внедряем сервис для подписи
    }

    // Получение API ключей и секретов из базы данных для пользователя
    private String getApiKey(Long userId, boolean isTest) {
        User user = userService.getUserByChatId(userId); // Получение пользователя по chatId
        return isTest ? user.getTestApiKey() : user.getRealApiKey(); // Возвращаем соответствующий API ключ
    }

    private String getApiSecret(Long userId, boolean isTest) {
        User user = userService.getUserByChatId(userId); // Получение пользователя по chatId
        return isTest ? user.getTestApiSecret() : user.getRealApiSecret(); // Возвращаем соответствующий API Secret
    }

    // Метод для выполнения GET-запросов к Binance API
    public String get(String endpoint, Long userId, boolean isTest) throws IOException {
        String apiUrl = isTest ? testBaseUrl : baseUrl;  // Выбор тестовой или реальной среды
        String apiKey = getApiKey(userId, isTest);  // Получаем API ключ
        String apiSecret = getApiSecret(userId, isTest);  // Получаем API Secret

        Request request = new Request.Builder()
                .url(apiUrl + endpoint)
                .header("X-MBX-APIKEY", apiKey)  // Добавляем API ключ в заголовки
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    // Метод для выполнения POST-запросов с JSON-телом
    public String post(String endpoint, String jsonBody, Long userId, boolean isTest) throws IOException {
        String apiUrl = isTest ? testBaseUrl : baseUrl;  // Выбор тестовой или реальной среды
        String apiKey = getApiKey(userId, isTest);  // Получаем API ключ
        String apiSecret = getApiSecret(userId, isTest);  // Получаем API Secret

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody, mediaType);
        Request request = new Request.Builder()
                .url(apiUrl + endpoint)
                .header("X-MBX-APIKEY", apiKey)  // Добавляем API ключ в заголовки
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    // Метод для выполнения POST-запросов с подписанными параметрами
    public String postSigned(String endpoint, String queryParams, Long userId, boolean isTest) throws IOException {
        String apiUrl = isTest ? testBaseUrl : baseUrl;
        String apiKey = getApiKey(userId, isTest);
        String apiSecret = getApiSecret(userId, isTest);

        // Получаем подпись с помощью BinanceSignatureService
        String signature = binanceSignatureService.sign(queryParams, apiSecret);

        String fullUrl = apiUrl + endpoint + "?" + queryParams + "&signature=" + signature;
        Request request = new Request.Builder()
                .url(fullUrl)
                .header("X-MBX-APIKEY", apiKey)
                .post(RequestBody.create("", null))  // Пустое тело для подписанных запросов
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    // Метод для выполнения запросов с PUT методом (если потребуется)
    public String put(String endpoint, String jsonBody, Long userId, boolean isTest) throws IOException {
        String apiUrl = isTest ? testBaseUrl : baseUrl;
        String apiKey = getApiKey(userId, isTest);

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody, mediaType);
        Request request = new Request.Builder()
                .url(apiUrl + endpoint)
                .header("X-MBX-APIKEY", apiKey)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    // Метод для выполнения DELETE-запросов (если потребуется)
    public String delete(String endpoint, Long userId, boolean isTest) throws IOException {
        String apiUrl = isTest ? testBaseUrl : baseUrl;
        String apiKey = getApiKey(userId, isTest);

        Request request = new Request.Builder()
                .url(apiUrl + endpoint)
                .header("X-MBX-APIKEY", apiKey)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }
}
