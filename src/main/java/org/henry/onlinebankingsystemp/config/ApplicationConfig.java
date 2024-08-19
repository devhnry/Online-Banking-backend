package org.henry.onlinebankingsystemp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.thymeleaf.TemplateEngine;

import java.util.concurrent.Executor;

@Configuration
public class ApplicationConfig {

    @Bean
    public TemplateEngine templateEngine() {
        return new TemplateEngine();
    }

    @Bean("customEmailExecutor")
    public Executor threadExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("onlineBanking-thread");
        return executor;
    }

}
