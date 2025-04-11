package com.chicu.neurotradebot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "binance")
public class BinanceProperties {


    private String baseUrl;
    private String testBaseUrl;

    // Дополнительные настройки, если они понадобятся для работы с Binance API
    private String websocketUrl;

}
