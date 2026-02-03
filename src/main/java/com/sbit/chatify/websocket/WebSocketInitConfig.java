package com.sbit.chatify.websocket;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class WebSocketInitConfig {

    private final TaskExecutor socketTaskExecutor;

    @PostConstruct
    public void init() {
        SocketUtil.init(socketTaskExecutor);
    }
}
