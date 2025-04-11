package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.trade.model.ClosedTrade;
import com.chicu.neurotradebot.trade.repository.ClosedTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClosedTradeService {

    private final ClosedTradeRepository closedTradeRepository;

    // Сохранение или обновление закрытой сделки
    public ClosedTrade save(ClosedTrade closedTrade) {
        return closedTradeRepository.save(closedTrade);
    }

    // Получение списка закрытых сделок для конкретного chatId
    public List<ClosedTrade> getClosedTradesByChatId(Long chatId) {
        return closedTradeRepository.findByChatId(chatId);
    }

    // Получение конкретной закрытой сделки по chatId и symbol
    public ClosedTrade getClosedTradeBySymbolAndChatId(String symbol, Long chatId) {
        return closedTradeRepository.findBySymbolAndChatId(symbol, chatId);
    }

    // Удаление всех закрытых сделок для конкретного chatId
    public void deleteClosedTradesByChatId(Long chatId) {
        List<ClosedTrade> closedTrades = getClosedTradesByChatId(chatId);
        closedTradeRepository.deleteAll(closedTrades);
    }

    // Удаление конкретной сделки по symbol и chatId
    public void deleteClosedTradeBySymbolAndChatId(String symbol, Long chatId) {
        ClosedTrade closedTrade = getClosedTradeBySymbolAndChatId(symbol, chatId);
        if (closedTrade != null) {
            closedTradeRepository.delete(closedTrade);
        }
    }
}
