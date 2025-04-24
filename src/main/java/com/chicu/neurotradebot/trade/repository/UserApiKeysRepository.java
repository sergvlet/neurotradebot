package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.model.UserApiKeys;
import com.chicu.neurotradebot.trade.enums.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserApiKeysRepository extends JpaRepository<UserApiKeys, Long> {

    // Ищем по chatId
    UserApiKeys findByChatId(Long chatId);

    // Добавляем метод для поиска по chatId и Exchange
    UserApiKeys findByChatIdAndExchange(Long chatId, Exchange exchange);
}
