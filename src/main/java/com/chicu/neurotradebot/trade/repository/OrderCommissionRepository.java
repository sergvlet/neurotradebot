package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.OrderCommission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderCommissionRepository extends JpaRepository<OrderCommission, Long> {

    // Найти комиссию по chatId и symbol
    Optional<OrderCommission> findByChatIdAndSymbol(Long chatId, String symbol);

    // Удалить все комиссии по chatId
    void deleteByChatId(Long chatId);

    // Удалить комиссию по chatId и symbol
    void deleteByChatIdAndSymbol(Long chatId, String symbol);
}
