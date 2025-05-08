package com.chicu.neurotradebot.telegram.handler.aimenu.strategyMenu;

import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu.RsiConfigMenuBuilder;
import com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu.StrategyMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class StrategyMenuCallbackHandler implements CallbackHandler {

  private final AiTradeSettingsService settingsService;
  private final TelegramSender sender;
  private final StrategyMenuBuilder strategyMenu;
  private final RsiConfigMenuBuilder   rsiConfigMenu;

  @Override
  public boolean canHandle(Update upd) {
    if (!upd.hasCallbackQuery()) return false;
    String data = upd.getCallbackQuery().getData();
    return data.equals("strategies:menu")
            || data.startsWith("toggle_strat_")
            || data.startsWith("config_strat_");
  }

  @Override
  public void handle(Update upd) {
    CallbackQuery cq  = upd.getCallbackQuery();
    Long chatId       = cq.getMessage().getChatId();
    Integer msgId     = cq.getMessage().getMessageId();
    String data       = cq.getData();

    // 1) Кнопка «Назад» в меню стратегий
    if (data.equals("strategies:menu")) {
      sender.editMessage(
              chatId, msgId,
              strategyMenu.title(),
              strategyMenu.markup(chatId)
      );
      return;
    }

    // 2) Переключение галочки стратегии
    if (data.startsWith("toggle_strat_")) {
      StrategyType type = StrategyType.valueOf(data.substring("toggle_strat_".length()));
      settingsService.toggleStrategy(chatId, type);
      // обновляем тот же сообщение с чекбоксами
      sender.editMessage(
              chatId, msgId,
              strategyMenu.title(),
              strategyMenu.markup(chatId)
      );
      return;
    }

    // 3) Открыть конфиг для конкретной стратегии
    if (data.startsWith("config_strat_")) {
      StrategyType type = StrategyType.valueOf(data.substring("config_strat_".length()));
      if (type == StrategyType.RSI) {
        // используем ваш метод buildOrEditMenu
        rsiConfigMenu.buildOrEditMenu(chatId, msgId);
      } else {
        // заглушка для остальных стратегий
        sender.editMessage(
                chatId, msgId,
                "⚙️ Конфигурация для «" + type.getDisplayName() + "» пока недоступна",
                strategyMenu.markup(chatId)
        );
      }
    }
  }
}
