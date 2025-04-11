package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.ClosedTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClosedTradeRepository extends JpaRepository<ClosedTrade, Long> {
    List<ClosedTrade> findByChatId(Long chatId);
    ClosedTrade findBySymbolAndChatId(String symbol, Long chatId);
}
