package com.sbit.chatify.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.service.SocketService;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SocketHandler extends TextWebSocketHandler {
    @Autowired
    private SocketService socketService;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        socketService.register(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            System.out.println("Received message: " + message.getPayload());
        } catch (Exception e) {
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        socketService.closeConnection(session, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        socketService.transportError(session, exception);
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
    }
}