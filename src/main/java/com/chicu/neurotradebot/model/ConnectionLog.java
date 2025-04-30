package com.chicu.neurotradebot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "connection_logs")
@Getter
@Setter
@NoArgsConstructor
public class ConnectionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String exchange;
    private Boolean testnet;
    private Boolean success;

    @Column(length = 500)
    private String message;

    private LocalDateTime createdAt = LocalDateTime.now();
}