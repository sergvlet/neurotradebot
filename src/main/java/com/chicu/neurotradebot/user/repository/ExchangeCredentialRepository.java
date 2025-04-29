package com.chicu.neurotradebot.user.repository;

import com.chicu.neurotradebot.telegram.handler.exchange.entity.ExchangeCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExchangeCredentialRepository extends JpaRepository<ExchangeCredential, Long> {
    Optional<ExchangeCredential> findByUserIdAndExchange(Long userId, String exchange);
}
