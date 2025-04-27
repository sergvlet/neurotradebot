package com.chicu.neurotradebot.trading.repository;

import com.chicu.neurotradebot.trading.entity.TradingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradingSessionRepository extends JpaRepository<TradingSession, Long> {
    List<TradingSession> findByUserId(Long userId);
}
