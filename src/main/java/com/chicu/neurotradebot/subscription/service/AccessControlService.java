package com.chicu.neurotradebot.subscription.service;

import com.chicu.neurotradebot.subscription.entity.SubscriptionStatus;
import com.chicu.neurotradebot.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessControlService {

    private final SubscriptionRepository subscriptionRepository;

    public boolean hasActiveSubscription(Long userId) {
        return !subscriptionRepository.findAllByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE).isEmpty();
    }
}
