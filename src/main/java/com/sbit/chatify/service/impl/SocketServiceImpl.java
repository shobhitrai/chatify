package com.sbit.chatify.service.impl;

import com.sbit.chatify.service.SocketService;
import com.sbit.chatify.websocket.SocketUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;

@Slf4j
@Service
public class SocketServiceImpl implements SocketService {

    @Override
    public void register(WebSocketSession session) {
        try {
            HttpSession httpSession = (HttpSession) session.getAttributes().get("httpSession");
            String userId = (String) httpSession.getAttribute("userId");
            if (Objects.nonNull(userId)) {
                SocketUtil.SOCKET_CONNECTION.put(userId, session);
                log.info("User connected: {}, Total users: {}", userId, SocketUtil.SOCKET_CONNECTION.size());
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
            HttpSession httpSession = (HttpSession) session.getAttributes().get("httpSession");
            String userId = (String) httpSession.getAttribute("userId");
            SocketUtil.SOCKET_CONNECTION.remove(userId);
            session.close(status);
            log.info("Connection closed for user: {}, code: {}, reason: {}, Total connected: {}",
                    userId, status.getCode(), status.getReason(), SocketUtil.SOCKET_CONNECTION.size());
        } catch (Exception e) {
            log.error("Error closing connection: {}", e.getMessage());
        }
    }

    @Override
    public void transportError(WebSocketSession session, Throwable exception) {
        HttpSession httpSession = (HttpSession) session.getAttributes().get("httpSession");
        String userId = (String) httpSession.getAttribute("userId");
        try {
            log.error("Transport error for user: {}. Error: {}", userId, exception.getMessage());
            session.close();
        } catch (Exception e) {
            log.error("Error during transport error handling: {}", e.getMessage());
        }
    }
}
