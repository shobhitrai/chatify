package com.sbit.chatify.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

@ServerEndpoint(value = "/chat", configurator = CustomConfigurator.class)
public class SocketHandler {

    @OnMessage
    public void onMessage(String message, Session session) {

    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig conf) {
        HandshakeRequest req = (HandshakeRequest) conf.getUserProperties().get("handshakereq");
        Map<String, List<String>> headers = req.getHeaders();
        String userId = "yututto";
        SocketUtil.register(userId, session);
    }

    @OnClose
    public void onClose(@PathParam("userId") String userId, Session session, CloseReason reason) {
        SocketUtil.closeConnection(userId, session, reason);
    }

    @OnError
    public void onError(@PathParam("userId") String userId, Session session, Throwable throwable) {
        SocketUtil.transportError(userId, session, throwable);
    }

    @OnMessage
    public void binaryMessage(Session session, ByteBuffer msg) {
        System.out.println("Binary message: " + msg.toString());
    }


}
