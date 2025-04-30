package com.sbit.chatify.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/socket/{userId}")
public class SocketHandler {

    @OnMessage
    public void onMessage(@PathParam("userId") String userId, String message, Session session) {

    }

    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
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


}
