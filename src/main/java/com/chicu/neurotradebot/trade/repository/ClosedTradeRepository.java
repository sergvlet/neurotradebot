package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.ClosedTrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClosedTradeRepository extends JpaRepository<ClosedTrade, Long> {
}