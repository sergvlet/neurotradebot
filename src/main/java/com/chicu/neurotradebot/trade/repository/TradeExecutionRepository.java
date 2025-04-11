package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.TradeExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeExecutionRepository extends JpaRepository<TradeExecution, Long> {
    
    // Найти все исполнения для конкретного chatId
    List<TradeExecution> findByChatId(Long chatId);
    
    // Найти все исполнения для конкретного symbol
    List<TradeExecution> findBySymbol(String symbol);

    // Удалить все исполнения для конкретного chatId
    void deleteByChatId(Long chatId);

    // Удалить все исполнения для конкретного symbol
    void deleteBySymbol(String symbol);
}
