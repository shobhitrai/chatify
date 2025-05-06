package com.sbit.chatify.service.impl;

import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.SocketConstant;
import com.sbit.chatify.model.SocketResponse;
import com.sbit.chatify.service.SocketService;
import com.sbit.chatify.websocket.SocketUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class SocketServiceImpl implements SocketService {

    @Override
    public void register(WebSocketSession session) {
        try {
            String userId = getUserIdFromSession(session);
            if (Objects.nonNull(userId)) {
                SocketUtil.SOCKET_CONNECTIONS.put(userId, session);
                log.info("User connected: {}, Session Id: {}, Total users: {}",
                        userId, session.getId(), SocketUtil.SOCKET_CONNECTIONS.size());
            } else {
                log.warn("UserId not found in session attributes.");
            }
        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage());
        }
    }

    @Override
    public void closeConnection(WebSocketSession session, CloseStatus status) {
        try {
            String userId = getUserIdFromSession(session);
            SocketUtil.SOCKET_CONNECTIONS.remove(userId);
            session.close(status);
            log.info("Connection closed for user: {}, code: {}, reason: {}, Total connected: {}",
                    userId, status.getCode(), status.getReason(), SocketUtil.SOCKET_CONNECTIONS.size());
        } catch (Exception e) {
            log.error("Error closing connection: {}", e.getMessage());
        }
    }

    @Override
    public void transportError(WebSocketSession session, Throwable exception) {
        try {
            String userId = getUserIdFromSession(session);
            log.error("Transport error for user: {}. Error: {}", userId, exception.getMessage());
            closeConnection(session);
        } catch (Exception e) {
            log.error("Error during transport error handling: {}", e.getMessage());
        }
    }

    private String getUserIdFromSession(WebSocketSession session) {
        HttpSession httpSession = (HttpSession) session.getAttributes().get(MessageConstant.HTTP_SESSION);
        return httpSession.getAttribute(MessageConstant.USER_ID).toString();
    }

    @Override
    public boolean validateHttpSession(WebSocketSession session) {
        HttpSession httpSession = (HttpSession) session.getAttributes().get(MessageConstant.HTTP_SESSION);
        if (httpSession == null || httpSession.getAttribute(MessageConstant.USER_ID) == null) {
            log.warn("Invalid HttpSession or userId. Closing WebSocket session.");
            closeConnection(session);
            return false;
        }
        return true;
    }

    private void closeConnection(WebSocketSession session) {
        String userId = SocketUtil.SOCKET_CONNECTIONS.entrySet().stream()
                .filter(entry -> Objects.equals(session, entry.getValue()))
                .map(Map.Entry::getKey).findFirst().orElse(null);

        if (Objects.isNull(userId)) {
            log.warn("Session not found in SOCKET_CONNECTIONS.");
            return;
        }

        // Implementing the CompletableFuture to close the session after sending the message
        CompletableFuture<Void> sendFuture = CompletableFuture.runAsync(() -> {
            SocketUtil.send(SocketResponse.builder()
                    .type(SocketConstant.INVALID_SESSION)
                    .userId(userId).build());
            log.info("Task executed by: " + Thread.currentThread().getName());
        });
        sendFuture.thenRun(() -> {
            closeSession(session, userId);
            log.info("Message sent and session closed for user: {}", userId);
        }).exceptionally(ex -> {
            log.error("Error while sending message: {}", ex.getMessage());
            closeSession(session, userId);
            return null;
        });
    }

    private static void closeSession(WebSocketSession session, String userId) {
        try {
            SocketUtil.SOCKET_CONNECTIONS.remove(userId);
            if (session != null && session.isOpen())
                session.close();
        } catch (IOException e) {
            log.error("Error closing session: {}", e.getMessage());
        }
    }

}
