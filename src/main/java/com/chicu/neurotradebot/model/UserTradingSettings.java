package com.chicu.neurotradebot.model;

import com.chicu.neurotradebot.service.AiTradeSettings;
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

    private String exchange;
    private Boolean useTestnet;
    private String tradingMode;

    @OneToOne(mappedBy = "userTradingSettings", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AiTradeSettings aiTradeSettings;
}
