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
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class SocketServiceImpl implements SocketService {

    @Override
    public void register(WebSocketSession session) {
        try {
            String userId = extractUserId(session);

            if (userId == null) {
                log.warn("WebSocket registration failed: userId missing");
                safeClose(session, CloseStatus.NOT_ACCEPTABLE);
                return;
            }

            SocketUtil.addConnection(userId, session);

            log.info("WebSocket connected | userId={} | sessionId={} | total={}",
                    userId, session.getId(), SocketUtil.getConnectionSize());

        } catch (Exception e) {
            log.error("WebSocket registration error", e);
            safeClose(session, CloseStatus.SERVER_ERROR);
        }
    }

    @Override
    public boolean validateHttpSession(WebSocketSession session) {
        String userId = extractUserId(session);

        if (userId == null) {
            log.warn("Invalid HttpSession detected");
            sendInvalidSessionAndClose(session);
            return false;
        }
        return true;
    }

    @Override
    public void transportError(WebSocketSession session, Throwable exception) {
        String userId = SocketUtil.getUserIdFromConnection(session);

        log.error(
                "WebSocket transport error | userId={} | sessionId={}",
                userId, session.getId(), exception
        );

        sendInvalidSessionAndClose(session);
    }

    @Override
    public void closeConnection(WebSocketSession session, CloseStatus status) {
        String userId = SocketUtil.getUserIdFromConnection(session);

        log.info(
                "WebSocket closing | userId={} | code={} | reason={}",
                userId, status.getCode(), status.getReason()
        );

        safeClose(session, status);
    }

    /* ================= INTERNAL ================= */

    private void sendInvalidSessionAndClose(WebSocketSession session) {
        String userId = SocketUtil.getUserIdFromConnection(session);

        if (userId != null) {
            SocketUtil.send(SocketResponse.builder()
                    .type(SocketConstant.INVALID_SESSION)
                    .userId(userId)
                    .build());
        }

        safeClose(session, CloseStatus.NORMAL);
    }

    private void safeClose(WebSocketSession session, CloseStatus status) {
        try {
            String userId = SocketUtil.getUserIdFromConnection(session);
            if (userId != null) {
                SocketUtil.removeConnection(userId);
            }

            if (session != null && session.isOpen()) {
                session.close(status);
            }

        } catch (IOException e) {
            log.error("Error closing WebSocket", e);
        }
    }

    private String extractUserId(WebSocketSession session) {
        return Optional.ofNullable(session.getAttributes().get(MessageConstant.USER_ID))
                .map(Object::toString)
                .orElse(null);
    }
}
