package com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.enums.ConfigWaiting;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.TelegramSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MlConfigMenuBuilder {

  private final AiTradeSettingsService settingsService;
  private final TelegramSender sender;

  public void buildOrEditMenu(Long chatId, Integer msgId) {
    AiTradeSettings cfg = settingsService.getByChatId(chatId);
    var ml = cfg.getMlStrategyConfig();

    String text = String.format(
      "⚙️ *Настройки ML TP/SL*\n" +
      "1️⃣ Капитал USD: %s\n" +
      "2️⃣ RSI порог: %s\n" +
      "3️⃣ Lookback: %s\n" +
      "4️⃣ Predict URL: %s",
      ml.getTotalCapitalUsd(),
      ml.getEntryRsiThreshold(),
      ml.getLookbackPeriod(),
      ml.getPredictUrl()
    );

    List<List<InlineKeyboardButton>> rows = List.of(
      List.of(InlineKeyboardButton.builder()
          .text("1️⃣ Капитал")
          .callbackData("set_ml_capital")
          .build()),
      List.of(InlineKeyboardButton.builder()
          .text("2️⃣ RSI порог")
          .callbackData("set_ml_rsi")
          .build()),
      List.of(InlineKeyboardButton.builder()
          .text("3️⃣ Lookback")
          .callbackData("set_ml_lookback")
          .build()),
      List.of(InlineKeyboardButton.builder()
          .text("4️⃣ Predict URL")
          .callbackData("set_ml_url")
          .build()),
      List.of(InlineKeyboardButton.builder()
          .text("⬅️ Назад")
          .callbackData("ai_strategies")
          .build())
    );

    InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
      .keyboard(rows)
      .build();

    sender.editMessage(chatId, msgId, text, markup);
  }
}
