package com.chicu.neurotradebot.subscription.service;

import com.chicu.neurotradebot.subscription.entity.PendingSubscription;
import com.chicu.neurotradebot.subscription.entity.Subscription;
import com.chicu.neurotradebot.subscription.entity.SubscriptionStatus;
import com.chicu.neurotradebot.subscription.repository.PendingSubscriptionRepository;
import com.chicu.neurotradebot.subscription.repository.SubscriptionRepository;
import com.chicu.neurotradebot.user.entity.User;
import com.chicu.neurotradebot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PendingSubscriptionRepository pendingSubscriptionRepository;
    private final UserRepository userRepository;

    public void createPendingSubscription(Long userId, Subscription.PlanType planType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Удаляем все старые Pending-подписки
        List<PendingSubscription> existingPending = pendingSubscriptionRepository.findAllByUserId(userId);
        if (!existingPending.isEmpty()) {
            pendingSubscriptionRepository.deleteAll(existingPending);
        }

        // Создаём новую Pending-подписку
        PendingSubscription pending = new PendingSubscription();
        pending.setUser(user);
        pending.setSelectedPlan(planType);
        pending.setCreatedAt(LocalDateTime.now());

        pendingSubscriptionRepository.save(pending);
    }

    public boolean confirmPayment(Long userId) {
        List<PendingSubscription> pendingList = pendingSubscriptionRepository.findAllByUserId(userId);

        if (pendingList.isEmpty()) {
            throw new RuntimeException("Нет ожидающей подписки для оплаты.");
        }

        // ✅ Проверяем, нет ли уже активной подписки
        boolean hasActive = subscriptionRepository.findAllByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .stream()
                .anyMatch(sub -> sub.getEndDate().isAfter(LocalDateTime.now()));

        if (hasActive) {
            throw new RuntimeException("У вас уже есть активная подписка. Нельзя оплатить новую пока не истекла старая.");
        }

        PendingSubscription pending = pendingList.get(0);

        Subscription subscription = new Subscription();
        subscription.setUser(pending.getUser());
        subscription.setPlan(pending.getSelectedPlan());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDateTime.now());

        // Устанавливаем срок действия подписки
        switch (pending.getSelectedPlan()) {
            case MONTHLY -> subscription.setEndDate(LocalDateTime.now().plusMonths(1));
            case QUARTERLY -> subscription.setEndDate(LocalDateTime.now().plusMonths(3));
            case HALF_YEAR -> subscription.setEndDate(LocalDateTime.now().plusMonths(6));
            case YEARLY -> subscription.setEndDate(LocalDateTime.now().plusYears(1));
            default -> throw new IllegalStateException("Неизвестный тип подписки");
        }

        subscriptionRepository.save(subscription);
        pendingSubscriptionRepository.delete(pending);

        return true;
    }

    public void createTrialSubscription(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Проверяем нет ли активной подписки
        subscriptionRepository.findAllByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .stream()
                .findFirst()
                .ifPresent(sub -> {
                    throw new RuntimeException("У вас уже есть активная подписка.");
                });

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(Subscription.PlanType.FREE_TRIAL);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(7)); // 7 дней пробная

        subscriptionRepository.save(subscription);
    }
}
