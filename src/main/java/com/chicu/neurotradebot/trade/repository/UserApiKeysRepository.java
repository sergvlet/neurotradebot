package com.chicu.neurotradebot.trade.repository;

import com.chicu.neurotradebot.trade.enums.Exchange;
import com.chicu.neurotradebot.trade.model.UserApiKeys;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserApiKeysRepository extends JpaRepository<UserApiKeys, Long> {
    Optional<UserApiKeys> findByChatIdAndExchange(Long chatId, Exchange exchange);
}
