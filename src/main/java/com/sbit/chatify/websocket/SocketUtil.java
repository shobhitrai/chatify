package com.sbit.chatify.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.model.SocketResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SocketUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, WebSocketSession> SOCKET_CONNECTIONS = new ConcurrentHashMap<>();

    public static int getConnectionSize() {
        return SOCKET_CONNECTIONS.size();
    }

    public static void addConnection(String userId, WebSocketSession session) {
        SOCKET_CONNECTIONS.put(userId, session);
        log.info("User {} connected. Total connections: {}", userId, getConnectionSize());
    }

    public static void removeConnection(String userId) {
        SOCKET_CONNECTIONS.remove(userId);
        log.info("User {} disconnected. Total connections: {}", userId, getConnectionSize());
    }

    public static boolean isUserConnected(String userId) {
        return SOCKET_CONNECTIONS.containsKey(userId);
    }

    public static String getUserIdFromConnection(WebSocketSession session) {
        return SOCKET_CONNECTIONS.entrySet().stream()
                .filter(entry -> Objects.equals(session, entry.getValue()))
                .map(Map.Entry::getKey).findFirst().orElse(null);
    }

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
            e.printStackTrace();
            log.error("Error sending message: {}", e.getMessage(), e);
        }
    }
}
