// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/pairsmenu/PairsAutoconfigCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.pairsmenu;

import com.chicu.neurotradebot.enums.ApiSetupStep;
import com.chicu.neurotradebot.trade.service.binance.BinanceClientProvider;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Component
@RequiredArgsConstructor
@Slf4j
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
        sender.execute(new AnswerCallbackQuery(cq.getId()));  // ack callback

        BotContext.setContext(chat, cq.getFrom());
        try {
            // 1) Получаем клиента по chatId, а не по user.getId()
            var client = clientProvider.getClientForUser(chat);

            // 2) Загружаем настройки и данные по объёму
            var user     = userService.getOrCreate(chat);
            var settings = settingsService.getOrCreate(user);
            String json = client.get24hrTicker();
            var arr = objectMapper.readValue(json, new TypeReference<List<Map<String,Object>>>() {});

            // 3) Формируем топ-5 пар
            List<String> top = arr.stream()
                    .map(m -> Map.entry(
                            (String)m.get("symbol"),
                            new BigDecimal(m.get("quoteVolume").toString())
                    ))
                    .sorted(Map.Entry.<String,BigDecimal>comparingByValue(Comparator.reverseOrder()))
                    .limit(5)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            // 4) Сохраняем пары
            settings.setPairs(new ArrayList<>(top));
            settings.setApiSetupStep(ApiSetupStep.NONE);
            settingsService.save(settings);

            // 5) Строим кнопки
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            for (int i = 0; i < top.size(); i += 2) {
                List<InlineKeyboardButton> row = new ArrayList<>();
                for (int j = 0; j < 2 && i + j < top.size(); j++) {
                    String sym   = top.get(i + j);
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
            InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();

            String joined = top.stream()
                    .map(s -> s.substring(0, s.length() - 4) + "/" + s.substring(s.length() - 4))
                    .collect(Collectors.joining(" / "));

            // 6) Редактируем сообщение
            EditMessageText edit = EditMessageText.builder()
                    .chatId(chat.toString())
                    .messageId(msgId)
                    .text("🤖 Топ-5 пар по объёму за 24h:\n" + joined)
                    .replyMarkup(markup)
                    .build();
            sender.execute(edit);

        } catch (IllegalStateException ex) {
            log.warn("API-ключи не настроены для Binance, chatId={}", chat);
            // Вместо редактирования клавиатуры просто сообщаем об ошибке
            sender.execute(EditMessageText.builder()
                    .chatId(chat.toString())
                    .messageId(msgId)
                    .text("❗ *API-ключи для Binance не настроены*\n" +
                            "Перейдите в меню «Настройки API» и укажите ключ и секрет.")
                    .parseMode("Markdown")
                    .build()
            );
        } catch (Exception ex) {
            log.error("Ошибка автоконфигурации пар для chatId={}", chat, ex);
            sender.execute(EditMessageText.builder()
                    .chatId(chat.toString())
                    .messageId(msgId)
                    .text("⚠️ Произошла ошибка при автоконфигурации пар.\nПожалуйста, попробуйте позже.")
                    .build()
            );
        } finally {
            BotContext.clear();
        }
    }
}
