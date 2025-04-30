package com.sbit.chatify.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.model.SocketResponse;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SocketUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static final Map<String, WebSocketSession> SOCKET_CONNECTION = new ConcurrentHashMap<>();

    public static void send(SocketResponse socketResponse) {
        try {
            WebSocketSession session = SOCKET_CONNECTION.get(socketResponse.getReceiverId());
            if (session != null && session.isOpen()) {
                String message = MAPPER.writeValueAsString(socketResponse);
                session.sendMessage(new TextMessage(message));
                log.info("Message sent to user: {}.", socketResponse.getReceiverId());
            } else {
                log.warn("Session not found or closed for user: {}", socketResponse.getReceiverId());
            }
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage(), e);
        }
    }
}
