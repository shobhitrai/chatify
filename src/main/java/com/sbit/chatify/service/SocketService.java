package com.sbit.chatify.service;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

public interface SocketService {
    void register(WebSocketSession session);

    void closeConnection(WebSocketSession session, CloseStatus status);

    void transportError(WebSocketSession session, Throwable exception);
}
