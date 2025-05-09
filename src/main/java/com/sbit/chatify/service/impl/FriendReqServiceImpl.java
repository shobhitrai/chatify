package com.sbit.chatify.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.SocketConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.dao.*;
import com.sbit.chatify.entity.Chat;
import com.sbit.chatify.entity.FriendRequest;
import com.sbit.chatify.entity.Notification;
import com.sbit.chatify.entity.UserDetail;
import com.sbit.chatify.model.*;
import com.sbit.chatify.service.FriendReqService;
import com.sbit.chatify.utility.Util;
import com.sbit.chatify.websocket.SocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private NotificationDao notificationDao;

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
                    .isActive(true).message(friendRequestDto.getMessage()).createdAt(new Date()).build();

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

    private void sendNotification(String userId, FriendRequestDto friendRequestDto,
                                  FriendRequest friendRequest) {
        try {
            var receiverId = friendRequestDto.getReceiverId();
            var senderDetail = userDetailDao.findByUserId(userId);

            var chat = Chat.builder()
                    .senderId(userId).receiverId(receiverId).message(friendRequest.getMessage())
                    .type(MessageConstant.FRIEND_REQUEST).createdAt(new Date()).isActive(true)
                    .isRead(false).build();
            chatDao.save(chat);

            var notification = Notification.builder()
                    .senderId(userId).receiverId(receiverId)
                    .message(senderDetail.getFirstName() + " " + MessageConstant.FRIEND_REQUEST_MESSAGE)
                    .createdAt(new Date()).isRead(false).build();
            notificationDao.save(notification);

            if (SocketUtil.SOCKET_CONNECTIONS.containsKey(receiverId)) {
                var notificationDto = getNotificationDto(notification, senderDetail, receiverId);
                var chatGroup = getChatDto(chat, senderDetail, receiverId);
                var socketNotiResponse = SocketResponse.builder().userId(receiverId)
                        .status(StatusConstant.SUCCESS_CODE).message(MessageConstant.SUCCESS)
                        .type(SocketConstant.APPEND_NOTIFICATION).data(notificationDto).build();
                SocketUtil.send(socketNotiResponse);
                var socketChatResponse = SocketResponse.builder().userId(receiverId)
                        .status(StatusConstant.SUCCESS_CODE).message(MessageConstant.SUCCESS)
                        .type(SocketConstant.APPEND_CHAT_GROUP).data(chatGroup).build();
                SocketUtil.send(socketChatResponse);
            }
        } catch (Exception e) {
            log.info("Error while sending friend request notification: {}", e.getMessage());
        }
    }

    private ChatGroup getChatDto(Chat chat, UserDetail senderDetail, String receiverId) {
        var chatMessage = ChatMessage.builder().type(chat.getType()).message(chat.getMessage())
                .createdAt(chat.getCreatedAt()).formattedDate(Util.getChatFormatedDate(chat.getCreatedAt()))
                .build();

        return ChatGroup.builder().senderId(senderDetail.getUserId())
                .senderFirstName(senderDetail.getFirstName()).receiverId(receiverId)
                .senderLastName(senderDetail.getLastName()).chats(List.of(chatMessage))
                .senderProfileImage(senderDetail.getProfileImage()).build();
    }

    private NotificationDto getNotificationDto(Notification notification, UserDetail senderDetail, String receiverId) {
       return NotificationDto.builder().createdAt(notification.getCreatedAt())
                .message(notification.getMessage()).senderId(notification.getSenderId())
                .receiverId(receiverId).isRead(notification.getIsRead())
                .formattedDate(Util.getNotificationFormatedDate(notification.getCreatedAt()))
                .senderProfileImage(senderDetail.getProfileImage())
                .isRecent(Util.isRecent(notification.getCreatedAt()))
                .isRead(notification.getIsRead()).build();
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
