package com.chicu.neurotradebot.subscription.repository;

import com.chicu.neurotradebot.subscription.entity.Subscription;
import com.chicu.neurotradebot.subscription.entity.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findAllByUserIdAndStatus(Long userId, SubscriptionStatus status);
}
