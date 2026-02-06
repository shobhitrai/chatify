package com.sbit.chatify.utility;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationShutdownState {
    @Getter
    private static volatile boolean isShuttingDown = false;

    @PreDestroy
    public void onShutdown() {
        isShuttingDown = true;
        log.info("Application is shutting down...");
    }
}
