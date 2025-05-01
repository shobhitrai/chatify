package com.sbit.chatify.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.model.SocketResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SocketUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static final Map<String, WebSocketSession> SOCKET_CONNECTIONS = new ConcurrentHashMap<>();

    public static void send(SocketResponse socketResponse) {
        try {
            WebSocketSession session = SOCKET_CONNECTIONS.get(socketResponse.getUserId());
            if (session != null && session.isOpen()) {
                String message = MAPPER.writeValueAsString(socketResponse);
                session.sendMessage(new TextMessage(message));
                log.info("Message sent to user: {}, message: {}.", socketResponse.getUserId(), message);
            } else {
                log.warn("Session not found or closed for user: {}", socketResponse.getUserId());
            }
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage(), e);
        }
    }
}
