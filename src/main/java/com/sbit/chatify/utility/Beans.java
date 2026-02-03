package com.sbit.chatify.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class Beans {
    @Bean
    public ObjectMapper modelMapper() {
        return new ObjectMapper();
    }

    @Bean
    public TaskExecutor socketTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("ws-send-");
        executor.initialize();
        return executor;
    }
}
