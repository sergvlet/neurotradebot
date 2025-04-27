package com.chicu.neurotradebot.trading.repository;

import com.chicu.neurotradebot.trading.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findBySessionId(Long sessionId);
}
