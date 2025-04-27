package com.chicu.neurotradebot.subscription.repository;

import com.chicu.neurotradebot.subscription.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findBySubscriptionId(Long subscriptionId);
}
