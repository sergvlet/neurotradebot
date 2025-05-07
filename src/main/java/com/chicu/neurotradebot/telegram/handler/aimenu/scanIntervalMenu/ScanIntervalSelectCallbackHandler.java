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
    String data   = cq.getData();            // например "scan_1m"
    long   chatId = cq.getMessage().getChatId();
    int    msgId  = cq.getMessage().getMessageId();

    // 1) Подтверждаем callback без текста
    sender.execute(new AnswerCallbackQuery(cq.getId()));

    // 2) Сохраняем новый интервал
    
    var user = userService.getOrCreate(chatId);
    var cfg  = settingsService.getOrCreate(user);

    Duration interval;
    switch (data) {
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

    // 3) Редактируем текущее сообщение: подтверждение + главное меню
    String confirmation = "⏱ Интервал сканирования установлен: " + format(interval);
    String newText = confirmation + "\n\n" + aiBuilder.title();

    sender.execute(EditMessageText.builder()
            .chatId(Long.toString(chatId))
            .messageId(msgId)
            .text(newText)                          // обновляем текст
            .replyMarkup(aiBuilder.markup(chatId))  // показываем главное меню
            .build()
    );

    BotContext.clear();
  }

  private String format(Duration d) {
    long mins = d.toMinutes();
    if (mins < 60) return mins + " мин.";
    return d.toHours() + " ч.";
  }
}
