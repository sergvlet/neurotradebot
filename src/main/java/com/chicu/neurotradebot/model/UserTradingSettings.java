package com.chicu.neurotradebot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_trading_settings")
@Getter
@Setter
@NoArgsConstructor
public class UserTradingSettings {

    @Id
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "use_testnet")
    private Boolean useTestnet;

    @Enumerated(EnumType.STRING)
    @Column(name = "trading_mode")
    private TradeMode tradeMode;

    @Column(name = "selected_exchange")
    private String selectedExchange;

    @OneToOne(mappedBy = "userTradingSettings", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AiTradeSettings aiTradeSettings;
}
