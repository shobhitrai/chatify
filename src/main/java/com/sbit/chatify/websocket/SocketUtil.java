package com.sbit.chatify.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.model.SocketResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public final class SocketUtil {

    /* ======================= CORE ======================= */

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // userId -> session
    private static final Map<String, WebSocketSession> USER_SESSIONS =
            new ConcurrentHashMap<>();

    // sessionId -> userId
    private static final Map<String, String> SESSION_USERS =
            new ConcurrentHashMap<>();

    // sessionId -> send lock
    private static final Map<String, Object> SESSION_LOCKS =
            new ConcurrentHashMap<>();

    private static TaskExecutor socketTaskExecutor;

    /* ======================= INIT ======================= */

    public static synchronized void init(TaskExecutor executor) {
        if (socketTaskExecutor != null) {
            log.warn("SocketUtil already initialized");
            return;
        }
        socketTaskExecutor = executor;
        log.info("SocketUtil initialized");
    }

    private static void ensureInitialized() {
        if (socketTaskExecutor == null) {
            throw new IllegalStateException("SocketUtil not initialized");
        }
    }

    /* ======================= CONNECTIONS ======================= */

    public static int getConnectionSize() {
        return USER_SESSIONS.size();
    }

    public static void addConnection(String userId, WebSocketSession session) {
        ensureInitialized();

        WebSocketSession oldSession = USER_SESSIONS.put(userId, session);
        if (oldSession != null && oldSession.isOpen()) {
            log.info("Replacing existing session for user {}", userId);
            safeClose(oldSession);
        }

        SESSION_USERS.put(session.getId(), userId);
        SESSION_LOCKS.put(session.getId(), new Object());
    }

    public static void removeConnection(String userId) {
        WebSocketSession session = USER_SESSIONS.remove(userId);
        if (session != null) {
            cleanupSession(session);
        }
    }

    public static void removeConnection(WebSocketSession session) {
        if (session != null) {
            cleanupSession(session);
        }
    }

    private static void cleanupSession(WebSocketSession session) {
        String sessionId = session.getId();
        String userId = SESSION_USERS.remove(sessionId);

        if (userId != null) {
            USER_SESSIONS.remove(userId, session);
        }

        SESSION_LOCKS.remove(sessionId);
        log.info("Session cleaned | sessionId={} | userId={} | active={}",
                sessionId, userId, getConnectionSize());
    }

    public static boolean isUserConnected(String userId) {
        return userId != null && USER_SESSIONS.containsKey(userId);
    }

    public static String getUserIdFromConnection(WebSocketSession session) {
        return session != null ? SESSION_USERS.get(session.getId()) : null;
    }

    /* ======================= SENDING ======================= */

    public static void send(SocketResponse response) {
        ensureInitialized();

        if (response == null || response.getUserId() == null) {
            log.warn("Invalid socket response");
            return;
        }

        WebSocketSession session = USER_SESSIONS.get(response.getUserId());
        if (session == null || !session.isOpen()) {
            log.warn("Session not available for user {}", response.getUserId());
            return;
        }

        String sessionId = session.getId();
        Object lock = SESSION_LOCKS.get(sessionId);

        if (lock == null) {
            log.warn("No send lock for session {}", sessionId);
            return;
        }

        socketTaskExecutor.execute(() -> {
            synchronized (lock) {
                try {
                    if (!session.isOpen()) {
                        return;
                    }

                    String payload = MAPPER.writeValueAsString(response);
                    session.sendMessage(new TextMessage(payload));

                } catch (Exception e) {
                    log.error("Failed to send message | userId={} | sessionId={}", response.getUserId(), sessionId, e);
                }
            }
        });
    }

    /* ======================= INTERNAL ======================= */

    private static void safeClose(WebSocketSession session) {
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (Exception e) {
            log.debug("Error closing session {}", session.getId(), e);
        }
    }
}
