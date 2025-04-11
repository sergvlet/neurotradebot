package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.trade.model.TradeExecution;
import com.chicu.neurotradebot.trade.repository.TradeExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeExecutionService {

    private final TradeExecutionRepository tradeExecutionRepository;

    // Найти все исполнения для chatId
    public List<TradeExecution> findByChatId(Long chatId) {
        return tradeExecutionRepository.findByChatId(chatId);
    }

    // Найти все исполнения для symbol
    public List<TradeExecution> findBySymbol(String symbol) {
        return tradeExecutionRepository.findBySymbol(symbol);
    }

    // Сохранить или обновить исполнение
    public TradeExecution save(TradeExecution tradeExecution) {
        return tradeExecutionRepository.save(tradeExecution);
    }

    // Удалить все исполнения для chatId
    public void deleteByChatId(Long chatId) {
        tradeExecutionRepository.deleteByChatId(chatId);
    }

    // Удалить все исполнения для symbol
    public void deleteBySymbol(String symbol) {
        tradeExecutionRepository.deleteBySymbol(symbol);
    }
}
