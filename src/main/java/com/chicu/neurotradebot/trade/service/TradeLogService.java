package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.trade.model.TradeLog;
import com.chicu.neurotradebot.trade.repository.TradeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class TradeLogService {

    private final TradeLogRepository repository;

    public void log(Long chatId, String symbol, String strategy, String signal, String mode, String details) {
        TradeLog log = new TradeLog();
        log.setChatId(chatId);
        log.setSymbol(symbol);
        log.setStrategy(strategy);
        log.setSignal(signal);
        log.setMode(mode);
        log.setDetails(details);
        log.setTimestamp(ZonedDateTime.now());

        repository.save(log);
    }
}
