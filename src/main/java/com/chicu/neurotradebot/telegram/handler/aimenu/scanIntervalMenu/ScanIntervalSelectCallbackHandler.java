// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/scanIntervalMenu/ScanIntervalSelectCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.scanIntervalMenu;

import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.AiTradeMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ScanIntervalSelectCallbackHandler implements CallbackHandler {

  private final UserService userService;
  private final AiTradeSettingsService settingsService;
  private final AiTradeMenuBuilder aiBuilder;
  private final TelegramSender sender;

  @Override
  public boolean canHandle(Update u) {
    return u.hasCallbackQuery()
            && u.getCallbackQuery().getData().startsWith("scan_");
  }

  @Override
  public void handle(Update u) throws Exception {
    var cq     = u.getCallbackQuery();
    String data   = cq.getData();            // например "scan_1s"
    long   chatId = cq.getMessage().getChatId();
    int    msgId  = cq.getMessage().getMessageId();

    sender.execute(new AnswerCallbackQuery(cq.getId()));

    var user = userService.getOrCreate(chatId);
    var cfg  = settingsService.getOrCreate(user);

    Duration interval;
    switch (data) {
      case "scan_1s"  -> interval = Duration.ofSeconds(1);
      case "scan_5s"  -> interval = Duration.ofSeconds(5);
      case "scan_10s" -> interval = Duration.ofSeconds(10);
      case "scan_20s" -> interval = Duration.ofSeconds(20);
      case "scan_1m"  -> interval = Duration.ofMinutes(1);
      case "scan_5m"  -> interval = Duration.ofMinutes(5);
      case "scan_15m" -> interval = Duration.ofMinutes(15);
      case "scan_1h"  -> interval = Duration.ofHours(1);
      default -> {
        BotContext.clear();
        return;
      }
    }

    cfg.setScanInterval(interval);
    settingsService.save(cfg);

    String confirmation = "⏱ Интервал сканирования: " + format(interval);
    String newText      = confirmation + "\n\n" + aiBuilder.title();

    sender.execute(EditMessageText.builder()
            .chatId(Long.toString(chatId))
            .messageId(msgId)
            .text(newText)
            .replyMarkup(aiBuilder.markup(chatId))
            .build()
    );

    BotContext.clear();
  }

  private String format(Duration d) {
    if (d.toSeconds() < 60) {
      return d.toSeconds() + " сек.";
    }
    long mins = d.toMinutes();
    if (mins < 60) {
      return mins + " мин.";
    }
    return d.toHours() + " ч.";
  }
}
