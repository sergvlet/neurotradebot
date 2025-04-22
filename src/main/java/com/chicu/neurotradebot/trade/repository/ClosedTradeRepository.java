package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.ClosedTrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClosedTradeRepository extends JpaRepository<ClosedTrade, Long> {

    List<ClosedTrade> findAllByChatId(Long chatId);

    List<ClosedTrade> findByChatId(Long chatId);}
