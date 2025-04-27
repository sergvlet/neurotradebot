package com.chicu.neurotradebot.subscription.repository;

import com.chicu.neurotradebot.subscription.entity.PendingSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PendingSubscriptionRepository extends JpaRepository<PendingSubscription, Long> {
    List<PendingSubscription> findAllByUserId(Long userId);
}
