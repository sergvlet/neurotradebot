// src/main/java/com/chicu/neurotradebot/entity/ApiCredentials.java
package com.chicu.neurotradebot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "api_credentials",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","exchange","test_mode"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApiCredentials {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User   user;

    @Column(name = "exchange", nullable = false, length = 32)
    private String exchange;

    @Column(name = "test_mode", nullable = false)
    private boolean testMode;     // false = real, true = test

    @Column(name = "api_key", nullable = false)
    private String apiKeyEncrypted;

    @Column(name = "api_secret", nullable = false)
    private String apiSecretEncrypted;
}
