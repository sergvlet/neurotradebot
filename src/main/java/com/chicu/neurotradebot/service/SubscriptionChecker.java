package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionChecker {

    private final UserRepository userRepository;

    public boolean hasActiveSubscription(Long chatId) {
        var user = userRepository.findById(chatId).orElse(null);

        if (user == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();

        return !user.isSubscriptionActive() || user.getSubscriptionEndAt() == null || !user.getSubscriptionEndAt().isAfter(now);
    }
}
