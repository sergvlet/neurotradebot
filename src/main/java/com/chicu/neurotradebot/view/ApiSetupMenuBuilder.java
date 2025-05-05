// 1) src/main/java/com/chicu/neurotradebot/view/ApiSetupMenuBuilder.java
package com.chicu.neurotradebot.view;

import com.chicu.neurotradebot.entity.ApiCredentials;
import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.service.ApiCredentialsService;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class ApiSetupMenuBuilder {

    private final UserService users;
    private final AiTradeSettingsService cfgs;
    private final ApiCredentialsService creds;

    public ApiSetupMenuBuilder(UserService u, AiTradeSettingsService c, ApiCredentialsService s) {
        this.users = u;
        this.cfgs  = c;
        this.creds = s;
    }

    public String title() {
        var user = users.getOrCreate(BotContext.getChatId());
        var cfg  = cfgs.getOrCreate(user);
        var has  = !creds.listCredentials(user, cfg.getExchange(), cfg.isTestMode()).isEmpty();
        return has
            ? "🔑 API-ключи найдены. Заменить или оставить?"
            : "🔑 Введите новый API-Key:";
    }

    public InlineKeyboardMarkup markup() {
        var user = users.getOrCreate(BotContext.getChatId());
        var cfg  = cfgs.getOrCreate(user);
        boolean has = !creds.listCredentials(user, cfg.getExchange(), cfg.isTestMode()).isEmpty();

        if (!has) {
            return InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(
                    InlineKeyboardButton.builder()
                        .text("🖊 Ввести Key")
                        .callbackData("enter_api_key")
                        .build()
                )))
                .build();
        }
        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(
                List.of(InlineKeyboardButton.builder()
                    .text("♻️ Заменить")
                    .callbackData("replace_api_key")
                    .build()),
                List.of(InlineKeyboardButton.builder()
                    .text("✅ Оставить")
                    .callbackData("keep_api_key")
                    .build())
            ))
            .build();
    }
}
