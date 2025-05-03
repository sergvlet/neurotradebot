// src/main/java/com/chicu/neurotradebot/config/TelegramProperties.java
package com.chicu.neurotradebot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "telegram.bot")
public class TelegramProperties {

    /** Имя бота из application.yml: telegram.bot.username */
    private String username;
    /** Токен бота: telegram.bot.token */
    private String token;


}
