// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/pairsmenu/PairsImportCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.pairsmenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.trade.service.binance.BinanceClientProvider;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;  // <- добавили
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PairsImportCallbackHandler implements CallbackHandler {

    private final AiTradeSettingsService settingsService;
    private final BinanceClientProvider clientProvider;
    private final TelegramSender sender;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean canHandle(Update update) {
        return update.hasCallbackQuery()
            && "pairs_import".equals(update.getCallbackQuery().getData());
    }

    @Override
    @Transactional // <— вот здесь
    public void handle(Update update) {
        CallbackQuery cq = update.getCallbackQuery();
        Long chatId     = cq.getMessage().getChatId();

        AiTradeSettings settings = settingsService.getByChatId(chatId);
        // теперь credentials будут подтянуты в рамках транзакции
        if (settings.getCredentials().isEmpty()) {
            sender.sendMessage(chatId,
                "❗️ API-ключи не настроены. Перейдите в меню API и добавьте ключи перед импортом пар.");
            return;
        }

        try {
            String infoJson = clientProvider.getClientForUser(chatId).getExchangeInfo();
            JsonNode root = objectMapper.readTree(infoJson);
            JsonNode symbolsNode = root.get("symbols");

            List<String> symbols = new ArrayList<>();
            if (symbolsNode != null && symbolsNode.isArray()) {
                for (JsonNode sym : symbolsNode) {
                    symbols.add(sym.get("symbol").asText());
                }
            }

            settings.setPairs(symbols);
            settingsService.save(settings);
            sender.sendMessage(chatId,
                "✅ Успешно импортировано " + symbols.size() + " валютных пар.");
        } catch (Exception ex) {
            sender.sendMessage(chatId,
                "⚠️ При импорте пар произошла ошибка. Попробуйте позже.");
            ex.printStackTrace();
        }
    }
}
