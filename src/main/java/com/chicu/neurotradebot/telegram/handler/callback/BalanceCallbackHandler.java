package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.exchange.core.ExchangeClient;
import com.chicu.neurotradebot.exchange.core.ExchangeRegistry;
import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BalanceCallbackHandler {

    private final ExchangeRegistry exchangeRegistry;
    private final StartMenuBuilder menuBuilder; // ✅ добавляем меню!

    public EditMessageText handle(Long chatId, Integer messageId) {
        ExchangeClient client = exchangeRegistry.getClient("BINANCE"); // пока бинанс
        Map<String, BigDecimal> balances = client.getAllBalances(chatId);

        String balancesText = balances.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue(Comparator.reverseOrder()))
                .limit(20) // топ-20
                .map(entry -> "🔹 " + entry.getKey() + ": *" + entry.getValue() + "*")
                .collect(Collectors.joining("\n"));

        if (balancesText.isEmpty()) {
            balancesText = "😢 Балансов не найдено.";
        }

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("""
                      💰 *Ваши доступные балансы:*

                      """ + balancesText)
                .replyMarkup(menuBuilder.buildMainMenu()) // ✅ прикрепляем главное меню
                .parseMode("Markdown")
                .build();
    }
}
