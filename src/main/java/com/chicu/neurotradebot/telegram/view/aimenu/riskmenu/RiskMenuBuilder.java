package com.chicu.neurotradebot.telegram.view.aimenu.riskmenu;

import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Set;

@Component
public class RiskMenuBuilder implements MenuDefinition {

    @Override
    public Set<String> keys() {
        return Set.of("ai_risk");
    }

    @Override
    public String title() {
        return "⚠️ Управление рисками";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        var sl = InlineKeyboardButton.builder()
                .text("🚨 Stop Loss %")
                .callbackData("risk_sl")
                .build();
        var tp = InlineKeyboardButton.builder()
                .text("🎯 Take Profit %")
                .callbackData("risk_tp")
                .build();
        var mp = InlineKeyboardButton.builder()
                .text("💰 Max % per trade")
                .callbackData("risk_mp")
                .build();
        var back = InlineKeyboardButton.builder()
                .text("⬅️ Назад")
                .callbackData("apply_network_settings_ai")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(sl),
                        List.of(tp),
                        List.of(mp),
                        List.of(back)
                ))
                .build();
    }
    public InlineKeyboardMarkup cancelMarkup(Long chatId) {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text("⬅️ Назад")
                                        .callbackData("ai_risk")
                                        .build()
                        )
                ))
                .build();
    }
}
