package com.chicu.neurotradebot.subscription.entity;

import com.chicu.neurotradebot.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "pending_subscriptions")
public class PendingSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private Subscription.PlanType selectedPlan;

    private LocalDateTime createdAt;
}
