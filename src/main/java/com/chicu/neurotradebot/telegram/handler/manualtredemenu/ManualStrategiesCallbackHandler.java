// src/main/java/com/chicu/neurotradebot/telegram/handler/manualtredemenu/ManualStrategiesCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.manualtredemenu;

import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.manualtredemenu.ManualStrategiesMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class ManualStrategiesCallbackHandler implements CallbackHandler {

    private final ManualStrategiesMenuBuilder menuBuilder;
    private final TelegramSender sender;

    @Override
    public boolean canHandle(Update u) {
        return u.hasCallbackQuery()
            && "manual_strategies".equals(u.getCallbackQuery().getData())
            || u.getCallbackQuery().getData().startsWith("manual_strat_");
    }

    @Override
    public void handle(Update u) throws Exception {
        var cq   = u.getCallbackQuery();
        Long chat = cq.getMessage().getChatId();
        int  msg  = cq.getMessage().getMessageId();
        String data = cq.getData();

        if ("manual_strategies".equals(data)) {
            // Показываем список стратегий
            sender.execute(EditMessageText.builder()
                .chatId(chat.toString())
                .messageId(msg)
                .text(menuBuilder.title())
                .replyMarkup(menuBuilder.markup(chat))
                .build());
        } else if (data.startsWith("manual_strat_")) {
            // Пользователь выбрал конкретную стратегию
            String stratKey = data.substring("manual_strat_".length());
            StrategyType type = StrategyType.valueOf(stratKey);

            String text = "Вы выбрали стратегию: " + type.getDisplayName()
                + "\nНастройте параметры и совершайте сделки вручную.";
            // Здесь можно предложить отдельное меню параметров или далее обрабатывать ввод

            sender.execute(EditMessageText.builder()
                .chatId(chat.toString())
                .messageId(msg)
                .text(text)
                .replyMarkup(menuBuilder.markup(chat))
                .build());
        }
    }
}
