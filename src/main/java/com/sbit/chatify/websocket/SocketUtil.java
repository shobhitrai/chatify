package com.sbit.chatify.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.model.SocketResponse;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SocketUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, Session> SOCKET_CONNECTION = new ConcurrentHashMap<>();

    public static void register(String userId, Session session) {
        try {
            SOCKET_CONNECTION.put(userId, session);
            log.info("User connected: {}, Total users: {}", userId, SOCKET_CONNECTION.size());
        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage());
        }
    }

    public static void send(SocketResponse socketResponse) {
        try {
            Session session = SOCKET_CONNECTION.get(socketResponse.getUserId());
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendObject(socketResponse);
            } else {
                log.warn("Session not found or closed for user: {}", socketResponse.getUserId());
            }
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage(), e);
        }
    }

    public static void closeConnection(String userId, Session session, CloseReason reason) {
        try {
            SOCKET_CONNECTION.remove(userId);
            session.close(reason);
            log.info("Connection closed for user: {} with reason: {}, Total connected: {}",
                    userId, reason.getReasonPhrase(), SOCKET_CONNECTION.size());
        } catch (Exception e) {
            log.error("Error closing connection: {}", e.getMessage());
        }
    }

    public static void transportError(String userId, Session session, Throwable throwable) {
        try {
            log.error("Transport error for user: {}. Error: {}", userId, throwable.getMessage());
//            session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage()));
        } catch (Exception e) {
            log.error("Error during transport error handling: {}", e.getMessage());
        }
    }
}
