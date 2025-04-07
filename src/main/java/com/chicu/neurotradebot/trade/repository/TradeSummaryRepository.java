package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.TradeSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeSummaryRepository extends JpaRepository<TradeSummary, Long> {
}