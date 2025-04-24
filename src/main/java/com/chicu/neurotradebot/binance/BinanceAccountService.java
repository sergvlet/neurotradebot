package com.chicu.neurotradebot.binance;

import com.chicu.neurotradebot.trade.enums.Exchange;
import com.chicu.neurotradebot.trade.enums.TradeMode;
import com.chicu.neurotradebot.trade.model.UserApiKeys;
import com.chicu.neurotradebot.trade.model.UserSettings;
import com.chicu.neurotradebot.trade.service.UserApiKeysService;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BinanceAccountService {

    private final BinanceHttpClient httpClient;
    private final UserApiKeysService apiKeysService;
    private final UserSettingsService settingsService;

    private static final String BASE_URL_REAL = "https://api.binance.com";
    private static final String BASE_URL_TEST = "https://testnet.binance.vision";

    /**
     * Возвращает отформатированный баланс пользователя на Binance.
     * @param chatId идентификатор чата
     * @return строка с балансом
     */
    public String getFormattedBalance(Long chatId) {
        String rawJson = getBalance(chatId);
        if (rawJson.startsWith("❌")) return rawJson;

        StringBuilder sb = new StringBuilder("💰 *Баланс на Binance:*\n\n");

        try {
            JSONObject json = new JSONObject(rawJson);
            JSONArray balances = json.getJSONArray("balances");

            for (int i = 0; i < balances.length(); i++) {
                JSONObject asset = balances.getJSONObject(i);
                double free = asset.getDouble("free");
                double locked = asset.getDouble("locked");
                String assetName = asset.getString("asset");

                if (free > 0 || locked > 0) {
                    sb.append(String.format("*%s*: свободно: `%.4f`, заблокировано: `%.4f`\n", assetName, free, locked));
                }
            }
        } catch (Exception e) {
            log.error("❌ Ошибка при парсинге баланса", e);
            return "❌ Ошибка при разборе баланса";
        }

        String result = sb.toString();
        if (result.length() > 4000) {
            result = result.substring(0, 3900) + "\n\n⚠️ Баланс обрезан: слишком много строк...";
        }

        return result;
    }

    /**
     * Получение баланса с учетом настроек пользователя
     * @param chatId идентификатор чата
     * @return ответ от API
     */
    public String getBalance(Long chatId) {
        UserSettings settings = settingsService.getOrCreate(chatId);
        Exchange exchange = settings.getExchange();
        TradeMode mode = settings.getTradeMode();

        // Получаем ключи пользователя для выбранной биржи и режима
        UserApiKeys keys = apiKeysService.getOrCreate(chatId, exchange);
        String apiKey = mode == TradeMode.REAL ? keys.getRealApiKey() : keys.getTestApiKey();
        String secretKey = mode == TradeMode.REAL ? keys.getRealApiSecret() : keys.getTestApiSecret();
        String baseUrl = mode == TradeMode.REAL ? BASE_URL_REAL : BASE_URL_TEST;

        if (apiKey == null || secretKey == null) {
            log.error("❌ API ключи не установлены для режима: " + mode.getTitle());
            return "❌ Ключи API не установлены для режима: " + mode.getTitle();
        }

        Map<String, String> params = new HashMap<>();
        // Отправляем запрос на получение баланса
        return httpClient.sendSignedRequest(baseUrl, "/api/v3/account", "GET", apiKey, secretKey, params);
    }
}
