// src/main/java/com/chicu/neurotradebot/telegram/handler/PairsAutoconfigCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.pairsmenu;

import com.chicu.neurotradebot.enums.ApiSetupStep;
import com.chicu.neurotradebot.exchange.binance.BinanceClientProvider;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Автоподбор пар по объёму за последние 24h.
 * Редактирует текущее сообщение, чтобы не спамить чат.
 */
@Component
@RequiredArgsConstructor
public class PairsAutoconfigCallbackHandler implements CallbackHandler {

    private final UserService userService;
    private final AiTradeSettingsService settingsService;
    private final BinanceClientProvider clientProvider;
    private final TelegramSender sender;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery() && "pairs_autoconfig".equals(u.getCallbackQuery().getData());
    }

    @Override
    @Transactional
    public void handle(Update u) throws Exception {
        var cq     = u.getCallbackQuery();
        Long chat  = cq.getMessage().getChatId();
        int  msgId = cq.getMessage().getMessageId();

        sender.execute(new AnswerCallbackQuery(cq.getId()));
        BotContext.setChatId(chat);

        try {
            var user     = userService.getOrCreate(chat);
            var settings = settingsService.getOrCreate(user);

            // Запрос к Binance
            String json = clientProvider.getClientForUser(user.getId()).get24hrTicker();
            var arr = objectMapper.readValue(json, new TypeReference<List<Map<String,Object>>>() {});

            // Топ-5 по объёму
            List<String> top = arr.stream()
                .map(m -> Map.entry(
                    (String)m.get("symbol"),
                    new BigDecimal(m.get("quoteVolume").toString())
                ))
                .sorted(Map.Entry.<String,BigDecimal>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

            // Затираем старый список и сохраняем новый
            settings.setPairs(new ArrayList<>(top));
            settings.setApiSetupStep(ApiSetupStep.NONE);
            settingsService.save(settings);

            // Кнопки: 2 в ряд, текст BASE/QUOTE
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            for (int i = 0; i < top.size(); i += 2) {
                List<InlineKeyboardButton> row = new ArrayList<>();
                for (int j = 0; j < 2 && i + j < top.size(); j++) {
                    String sym = top.get(i + j);
                    String base  = sym.substring(0, sym.length() - 4);
                    String quote = sym.substring(sym.length() - 4);
                    row.add(InlineKeyboardButton.builder()
                        .text(base + "/" + quote)
                        .callbackData("pair_select_" + sym)
                        .build());
                }
                rows.add(row);
            }
            rows.add(Collections.singletonList(
                InlineKeyboardButton.builder()
                    .text("⬅️ Назад")
                    .callbackData("ai_pairs")
                    .build()
            ));

            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();

            // Редактируем сообщение
            String joined = top.stream()
                .map(s -> s.substring(0, s.length() - 4) + "/" + s.substring(s.length() - 4))
                .collect(Collectors.joining(" / "));

            EditMessageText edit = EditMessageText.builder()
                .chatId(chat.toString())
                .messageId(msgId)
                .text("🤖 Топ-5 пар по объёму за 24h:\n" + joined)
                .replyMarkup(markup)
                .build();

            sender.execute(edit);

        } finally {
            BotContext.clear();
        }
    }
}
