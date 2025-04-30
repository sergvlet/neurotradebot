package com.chicu.neurotradebot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ai_trade_settings")
@Getter
@Setter
@NoArgsConstructor
public class AiTradeSettings {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserTradingSettings userTradingSettings;

    private String strategy;
    private String risk;
    private String tradingType;

    private Boolean autostart;
    private Boolean notifications;

    private String pairMode;
    private String manualPair;

    @Column(length = 1000)
    private String allowedPairs;
}
