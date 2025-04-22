package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.ActiveTrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActiveTradeRepository extends JpaRepository<ActiveTrade, Long> {
    Optional<ActiveTrade> findByChatIdAndSymbol(Long chatId, String symbol);


}
