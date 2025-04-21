package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.MarketCandle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface MarketCandleRepository extends JpaRepository<MarketCandle, Long> {

    List<MarketCandle> findBySymbolAndIntervalAndTimestampBetweenOrderByTimestampAsc(
            String symbol, String interval, ZonedDateTime from, ZonedDateTime to);

    List<MarketCandle> findTop200BySymbolAndIntervalOrderByTimestampDesc(
            String symbol, String interval);
}
