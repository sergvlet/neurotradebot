package com.chicu.neurotradebot.repository;

import com.chicu.neurotradebot.model.ExchangeCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeCredentialRepository extends JpaRepository<ExchangeCredential, Long> {

    // Найти по user_id и названию биржи
    Optional<ExchangeCredential> findByUserIdAndExchange(Long userId, String exchange);

     List<ExchangeCredential> findByUserId(Long userId);
}
