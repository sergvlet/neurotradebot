package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.trade.model.ActiveTrade;
import com.chicu.neurotradebot.trade.repository.ActiveTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActiveTradeService {

    private final ActiveTradeRepository activeTradeRepository;

    // Сохраняем или обновляем активную сделку
    public ActiveTrade save(ActiveTrade activeTrade) {
        return activeTradeRepository.save(activeTrade);
    }

    // Получаем активную сделку по chatId и символу
    public Optional<ActiveTrade> getActive(Long chatId, String symbol) {
        return activeTradeRepository.findByChatIdAndSymbol(chatId, symbol);
    }

    // Метод для проверки, существует ли активная сделка по chatId и symbol
    public boolean existsActiveTrade(Long chatId, String symbol) {
        return activeTradeRepository.existsByChatIdAndSymbol(chatId, symbol);
    }

    // Метод для удаления всех активных сделок по chatId
    public void deleteByChatId(Long chatId) {
        activeTradeRepository.deleteByChatId(chatId);
    }
    // Метод для удаления активных сделок по chatId и symbol
    public void deleteByChatIdAndSymbol(Long chatId, String symbol) {
        activeTradeRepository.deleteByChatIdAndSymbol(chatId, symbol);
    }
}
