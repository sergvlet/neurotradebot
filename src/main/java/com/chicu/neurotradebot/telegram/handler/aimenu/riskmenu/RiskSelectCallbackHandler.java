package com.chicu.neurotradebot.telegram.handler.aimenu.riskmenu;

import com.chicu.neurotradebot.enums.ApiSetupStep;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.riskmenu.RiskMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class RiskSelectCallbackHandler implements CallbackHandler {

  private final UserService userService;
  private final AiTradeSettingsService settingsService;
  private final TelegramSender sender;
  private final RiskMenuBuilder riskBuilder;

  @Override
  public boolean canHandle(Update u) {
    return u.hasCallbackQuery() && u.getCallbackQuery().getData().startsWith("risk_");
  }

  @Override
  public void handle(Update u) throws Exception {
    var cq     = u.getCallbackQuery();
    String data   = cq.getData();      // "risk_sl", "risk_tp" или "risk_mp"
    long   chatId = cq.getMessage().getChatId();
    int    msgId  = cq.getMessage().getMessageId();

    // 1) Подтверждаем callback без спама
    sender.execute(new AnswerCallbackQuery(cq.getId()));

    // 2) Обновляем состояние
    BotContext.setChatId(chatId);
    var cfg = settingsService.getOrCreate(userService.getOrCreate(chatId));
    ApiSetupStep step;
    String promptText;
    switch (data) {
      case "risk_sl" -> {
        step = ApiSetupStep.ENTER_RISK_SL;
        promptText = "Введите % Stop-Loss:";
      }
      case "risk_tp" -> {
        step = ApiSetupStep.ENTER_RISK_TP;
        promptText = "Введите % Take-Profit:";
      }
      case "risk_mp" -> {
        step = ApiSetupStep.ENTER_RISK_MAXP;
        promptText = "Введите максимальный % на сделку:";
      }
      default -> {
        BotContext.clear();
        return;
      }
    }
    cfg.setApiSetupStep(step);
    settingsService.save(cfg);

    // 3) Редактируем старое сообщение, выводя только prompt
    sender.execute(EditMessageText.builder()
        .chatId(Long.toString(chatId))
        .messageId(msgId)
        .text(promptText)
        // можно дать кнопку «Отмена» через RiskMenuBuilder.cancelButton(chatId)
        .replyMarkup(riskBuilder.cancelMarkup(chatId))
        .build()
    );

    BotContext.clear();
  }
}
