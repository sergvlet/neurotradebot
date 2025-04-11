package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.trade.model.TradeLog;
import com.chicu.neurotradebot.trade.repository.TradeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeLogService {

    private final TradeLogRepository tradeLogRepository;

    // Сохранить новый лог сделки
    public TradeLog save(TradeLog tradeLog) {
        return tradeLogRepository.save(tradeLog);
    }

    // Получить все логи сделок
    public List<TradeLog> getAllLogs() {
        return tradeLogRepository.findAll();
    }

    // Получить логи сделок для конкретной активной сделки
    public List<TradeLog> getLogsByActiveTradeId(Long activeTradeId) {
        return tradeLogRepository.findByActiveTradeId(activeTradeId); // Этот метод был пропущен
    }
}
