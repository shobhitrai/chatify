package com.sbit.chatify.websocket;

import com.sbit.chatify.constant.MessageConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            log.warn("WebSocket handshake rejected: not a servlet request");
            return false;
        }

        HttpServletRequest httpRequest = servletRequest.getServletRequest();
        HttpSession httpSession = httpRequest.getSession(false);

        if (httpSession == null) {
            log.warn("WebSocket handshake rejected: HttpSession not found");
            return false;
        }

        Object userId = httpSession.getAttribute(MessageConstant.USER_ID);
        if (userId == null) {
            log.warn("WebSocket handshake rejected: USER_ID missing | httpSessionId={}", httpSession.getId());
            return false;
        }

        // Store only immutable data in WebSocket attributes
        attributes.put(MessageConstant.USER_ID, userId.toString());
        log.info("WebSocket handshake accepted | userId={} | httpSessionId={}", userId, httpSession.getId());
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket handshake failed", exception);
        }
    }
}
