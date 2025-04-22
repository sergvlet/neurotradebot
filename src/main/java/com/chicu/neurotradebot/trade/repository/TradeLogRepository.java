package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.TradeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeLogRepository extends JpaRepository<TradeLog, Long> {
    List<TradeLog> findByChatIdOrderByTimestampDesc(Long chatId);
    List<TradeLog> findAllByChatId(Long chatId);

}
