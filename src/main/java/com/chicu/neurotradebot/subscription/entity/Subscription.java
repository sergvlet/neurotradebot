package com.chicu.neurotradebot.subscription.entity;

import com.chicu.neurotradebot.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private PlanType plan; // FREE_TRIAL / MONTHLY / QUARTERLY / HALF_YEAR / YEARLY

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status; // ACTIVE / EXPIRED / CANCELED

    private LocalDateTime startDate;
    private LocalDateTime endDate;



    public enum PlanType {
        FREE_TRIAL,
        MONTHLY,
        QUARTERLY,
        HALF_YEAR,
        YEARLY
    }

}
