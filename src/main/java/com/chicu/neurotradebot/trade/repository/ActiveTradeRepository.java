package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.ActiveTrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActiveTradeRepository extends JpaRepository<ActiveTrade, Long> {
}