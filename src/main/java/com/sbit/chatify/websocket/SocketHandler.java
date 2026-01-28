package com.sbit.chatify.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.SocketConstant;
import com.sbit.chatify.model.ContactDto;
import com.sbit.chatify.model.FriendRequestDto;
import com.sbit.chatify.model.SocketRequest;
import com.sbit.chatify.model.UserDto;
import com.sbit.chatify.service.ChatService;
import com.sbit.chatify.service.FriendReqService;
import com.sbit.chatify.service.SocketService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class SocketHandler extends TextWebSocketHandler {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SocketService socketService;

    @Autowired
    private FriendReqService friendReqService;

    @Autowired
    private ChatService chatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        socketService.register(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            boolean httpSessionStatus = socketService.validateHttpSession(session);
            if (!httpSessionStatus)
                return;
            log.info("Received from socket message: {}", message.getPayload());
            processMessage(session, message);

        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        socketService.closeConnection(session, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        socketService.transportError(session, exception);
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {

    }

    private void processMessage(WebSocketSession session, TextMessage message) throws Exception {
        SocketRequest socketRequest = mapper.readValue(message.getPayload(), SocketRequest.class);
        String userId = getUserIdFromSession(session);
        switch (socketRequest.getType()) {
            case SocketConstant.FRIEND_REQUEST:
                friendReqService.sendFriendRequest(userId,
                        mapper.convertValue(socketRequest.getPayload(), FriendRequestDto.class));
                break;

            case SocketConstant.SEARCHED_USERS:
                friendReqService.getSearchedUsers(userId,
                        mapper.convertValue(socketRequest.getPayload(), UserDto.class));
                break;

            case SocketConstant.ACCEPT_FRIEND_REQUEST:
                friendReqService.acceptFriendRequest(userId,
                        mapper.convertValue(socketRequest.getPayload(), FriendRequestDto.class));
                break;

            case SocketConstant.REJECT_FRIEND_REQUEST:
                friendReqService.rejectFriendRequest(userId,
                        mapper.convertValue(socketRequest.getPayload(), FriendRequestDto.class));
                break;
            case SocketConstant.CANCEL_FRIEND_REQUEST:
                friendReqService.cancelFriendRequest(userId,
                        mapper.convertValue(socketRequest.getPayload(), FriendRequestDto.class));
                break;
            case SocketConstant.SEEN_LAST_MSG:
                chatService.seenLastMsg(userId,
                        mapper.convertValue(socketRequest.getPayload(), ContactDto.class));
                break;


        }
    }

    private String getUserIdFromSession(WebSocketSession session) {
        HttpSession httpSession = (HttpSession) session.getAttributes().get(MessageConstant.HTTP_SESSION);
        return httpSession.getAttribute(MessageConstant.USER_ID).toString();
    }
}