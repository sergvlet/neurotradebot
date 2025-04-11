package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.TradeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeLogRepository extends JpaRepository<TradeLog, Long> {

    // Получение всех логов для активной сделки по её id
    List<TradeLog> findByActiveTradeId(Long activeTradeId);
}
