package com.sbit.chatify.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.SocketConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.dao.ChatDao;
import com.sbit.chatify.dao.FriendRequestDao;
import com.sbit.chatify.dao.UserDao;
import com.sbit.chatify.dao.UserDetailDao;
import com.sbit.chatify.entity.Chat;
import com.sbit.chatify.entity.FriendRequest;
import com.sbit.chatify.entity.UserDetail;
import com.sbit.chatify.model.*;
import com.sbit.chatify.service.FriendReqService;
import com.sbit.chatify.websocket.SocketUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Slf4j
@Service
public class FriendReqServiceImpl implements FriendReqService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private FriendRequestDao friendRequestDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ChatDao chatDao;

    @Autowired
    private UserDetailDao userDetailDao;

    @Autowired
    private HttpSession session;

    @Override
    public void sendFriendRequest(String userId, FriendRequestDto friendRequestDto) {
        try {
            var friendReqAlredyExists = friendRequestDao.findBySenderIdAndReceiverId(userId,
                    friendRequestDto.getReceiverId());

            if (friendReqAlredyExists) {
                var socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.FAILURE_CODE)
                        .message(MessageConstant.FRIEND_REQUEST_ALREADY_EXISTS)
                        .type(SocketConstant.ACK_FRIEND_REQUEST).build();
                SocketUtil.send(socketResponse);
                return;
            }
            var friendRequest = FriendRequest.builder().senderId(userId)
                    .receiverId(friendRequestDto.getReceiverId()).isAccepted(false)
                    .isActive(true).createdAt(new Date()).build();

            friendRequestDao.save(friendRequest);
            var socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.SUCCESS_CODE)
                    .message(MessageConstant.SUCCESS).type(SocketConstant.ACK_FRIEND_REQUEST).build();
            SocketUtil.send(socketResponse);

            sendNotification(userId, friendRequestDto, friendRequest);
        } catch (Exception e) {
            log.info("Error while sending friend request: {}", e.getMessage());
            var socketResponse = SocketResponse.builder().userId(userId)
                    .status(StatusConstant.INTERNAL_SERVER_ERROR_CODE)
                    .message(MessageConstant.INTERNAL_SERVER_ERROR)
                    .type(SocketConstant.ACK_FRIEND_REQUEST).build();
            SocketUtil.send(socketResponse);
        }
    }

    private void sendNotification(String userId,
                                  FriendRequestDto friendRequestDto, FriendRequest friendRequest) {
        try {
            var receiverId = friendRequestDto.getReceiverId();
            var chat = Chat.builder().message(friendRequest.getMessage())
                    .senderId(userId).receiverId(receiverId)
                    .type(MessageConstant.FRIEND_REQUEST).isRead(false)
                    .isActive(true).createdAt(new Date()).build();
            chatDao.save(chat);

            if (SocketUtil.SOCKET_CONNECTIONS.containsKey(receiverId)) {
                var senderDetail = userDetailDao.findByUserId(userId);
                var chatDto = getChat(friendRequestDto.getReceiverId(), chat, senderDetail);
                var socketResponse = SocketResponse.builder().userId(receiverId)
                        .status(StatusConstant.SUCCESS_CODE).message(MessageConstant.FRIEND_REQUEST)
                        .data(chatDto).type(SocketConstant.CHAT).build();
                SocketUtil.send(socketResponse);
            }
        } catch (Exception e) {
            log.info("Error while sending friend request notification: {}", e.getMessage());
        }
    }

    private ChatDto getChat(String receiverId, Chat chat, UserDetail senderDetail) {
        return ChatDto.builder().senderId(senderDetail.getUserId())
                .senderFirstName(senderDetail.getFirstName()).senderLastName(senderDetail.getLastName())
                .receiverId(receiverId).message(chat.getMessage()).isRead(chat.getIsRead())
                .type(chat.getType()).formattedDate("Now").build();
    }

    @Override
    public void getSearchedUsers(String userId, UserDto userDto) {
        SocketResponse socketResponse = null;
        try {
            var users = userDao.findByUserName(userDto.getUsername());
            var foundUsers = new ArrayList<>();
            users.stream().filter(user -> !user.getId().toString().equals(userId))
                    .forEach(user -> {
                        var dto = mapper.convertValue(user, UserDto.class);
                        var userDetail = userDetailDao.findByUserId(user.getId().toString());
                        dto.setFirstName(userDetail.getFirstName());
                        dto.setLastName(userDetail.getLastName());
                        dto.setProfileImage(userDetail.getProfileImage());
                        dto.setUsername(user.getUsername());
                        dto.setUserId(user.getId().toString());
                        foundUsers.add(dto);
                    });

            if (foundUsers.isEmpty())
                socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.FAILURE_CODE)
                        .message(MessageConstant.NO_USER_FOUND)
                        .type(SocketConstant.ACK_SEARCHED_USERS).build();
            else
                socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.SUCCESS_CODE)
                        .message(MessageConstant.SUCCESS).type(SocketConstant.ACK_SEARCHED_USERS)
                        .data(foundUsers).build();

        } catch (Exception e) {
            log.error("Error while searching users: {}", e.getMessage());
            socketResponse = SocketResponse.builder().userId(userId)
                    .status(StatusConstant.INTERNAL_SERVER_ERROR_CODE)
                    .message(MessageConstant.INTERNAL_SERVER_ERROR)
                    .type(SocketConstant.ACK_FRIEND_REQUEST).build();
        } finally {
            SocketUtil.send(socketResponse);
        }
    }

}
