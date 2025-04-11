package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.ActiveTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActiveTradeRepository extends JpaRepository<ActiveTrade, Long> {

    Optional<ActiveTrade> findByChatIdAndSymbol(Long chatId, String symbol); // метод для получения активной сделки
    boolean existsByChatIdAndSymbol(Long chatId, String symbol); // метод для проверки существования активной сделки
    void deleteByChatId(Long chatId); // метод для удаления сделок по chatId
    void deleteByChatIdAndSymbol(Long chatId, String symbol); // метод для удаления сделок по chatId и символу
}
