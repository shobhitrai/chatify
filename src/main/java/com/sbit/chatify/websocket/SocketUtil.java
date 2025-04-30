package com.sbit.chatify.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.model.SocketResponse;
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
    private static final Map<String, WebSocketSession> SOCKET_CONNECTION = new ConcurrentHashMap<>();

    public static String register(WebSocketSession session) {
        try {
            String[] urlParts = session.getUri().toString().split("/");
            String userId = urlParts[urlParts.length - 1];
            SOCKET_CONNECTION.put(userId, session);
            log.info("User connected: {}, Total users: {}", userId, SOCKET_CONNECTION.size());
            return userId;
        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage(), e);
            return null;
        }
    }

    public static void send(SocketResponse socketResponse) {
        try {
            WebSocketSession session = SOCKET_CONNECTION.get(socketResponse.getUserId());
            if (session != null && session.isOpen()) {
                String payload = MAPPER.writeValueAsString(socketResponse.getPayload());
                session.sendMessage(new TextMessage(payload));
            } else {
                log.warn("Session not found or closed for user: {}", socketResponse.getUserId());
            }
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage(), e);
        }
    }

    public static void closeConnection(WebSocketSession session, CloseStatus status) {
        try {
            String userId = SOCKET_CONNECTION.entrySet().stream()
                    .filter(entry -> Objects.equals(entry.getValue(), session))
                    .map(Map.Entry::getKey).findFirst().orElse(null);

            if (userId != null) {
                SOCKET_CONNECTION.remove(userId);
                session.close(status);
                log.info("Connection closed for user: {}, Total connected: {}", userId, SOCKET_CONNECTION.size());
            } else {
                log.warn("User not found for session: {}", session.getId());
            }
        } catch (Exception e) {
            log.error("Error closing connection: {}", e.getMessage(), e);
        }
    }

    public static void transportError(WebSocketSession session, Throwable exception) {
        try {
            String userId = SOCKET_CONNECTION.entrySet().stream()
                    .filter(entry -> Objects.equals(entry.getValue(), session))
                    .map(Map.Entry::getKey).findFirst().orElse(null);
            log.error("Error in transport for user: {}, Exception: {}", userId, exception.getMessage(), exception);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
