// src/main/java/com/chicu/neurotradebot/config/SchedulerConfig.java
package com.chicu.neurotradebot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        var ts = new ThreadPoolTaskScheduler();
        ts.setPoolSize(10);
        ts.setThreadNamePrefix("trading-scheduler-");
        return ts;
    }
}
