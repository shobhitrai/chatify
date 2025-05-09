package com.sbit.chatify.service.impl;

import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.PageConstant;
import com.sbit.chatify.dao.*;
import com.sbit.chatify.entity.Chat;
import com.sbit.chatify.entity.User;
import com.sbit.chatify.model.*;
import com.sbit.chatify.service.WallService;
import com.sbit.chatify.utility.Util;
import com.sbit.chatify.websocket.SocketUtil;
import jakarta.servlet.http.HttpSession;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class WallServiceImpl implements WallService {

    @Autowired
    private HttpSession session;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserDetailDao userDetailDao;

    @Autowired
    private ChatDao chatDao;

    @Autowired
    private FriendRequestDao friendRequestDao;

    @Autowired
    private NotificationDao notificationDao;

    @Override
    public String getWallData(Model model) {
        var userId = (String) session.getAttribute(MessageConstant.USER_ID);
        if (Objects.isNull(userId) || SocketUtil.SOCKET_CONNECTIONS.containsKey(userId))
            return PageConstant.REDIRECT_LOGIN;

        var user = userDao.findById(new ObjectId(userId));
        var userDto = getUserDetails(user);
        var friendRequests = getFriendRequests(userId);
        var chats = getAllLatestChat(userId, friendRequests);
        var notifications = getAllNotifications(userId);

        model.addAttribute(MessageConstant.USER, userDto);
        model.addAttribute(MessageConstant.FRIEND_REQUESTS, friendRequests);
        model.addAttribute(MessageConstant.CHATS, chats);
        model.addAttribute(MessageConstant.NOTIFICATIONS, notifications);
        return PageConstant.WALL;
    }

    private List<NotificationDto> getAllNotifications(String userId) {
        var notifications = notificationDao.findByReceiverId(userId);
        return notifications.stream().map(notification -> {
            var senderDetails = userDetailDao.findByUserId(notification.getSenderId());
            return NotificationDto.builder().createdAt(notification.getCreatedAt())
                    .message(notification.getMessage()).senderId(notification.getSenderId())
                    .receiverId(userId).isRead(notification.getIsRead())
                    .formattedDate(Util.getFormatedDate(notification.getCreatedAt()))
                    .senderProfileImage(senderDetails.getProfileImage())
                    .isRecent(Util.isRecent(notification.getCreatedAt()))
                    .isRead(notification.getIsRead()).build();
        }).toList();
    }

    private List<FriendRequestDto> getFriendRequests(String userId) {
        var friendRequests = friendRequestDao.findByReceiverId(userId);
        return friendRequests.stream().map(fr -> {
            var senderDetails = userDetailDao.findByUserId(fr.getSenderId());

            return FriendRequestDto.builder().createdAt(fr.getCreatedAt())
                    .message(fr.getMessage()).senderId(fr.getSenderId())
                    .senderFirstName(senderDetails.getFirstName())
                    .senderLastName(senderDetails.getLastName())
                    .formattedDate(Util.getFormatedDate(fr.getCreatedAt()))
                    .senderProfileImage(senderDetails.getProfileImage())
                    .isRecent(Util.isRecent(fr.getCreatedAt())).build();
        }).sorted(Comparator.comparing(FriendRequestDto::getCreatedAt).reversed()).toList();
    }

    private List<ChatGroup> getAllLatestChat(String userId, List<FriendRequestDto> friendRequests) {
        var chats = chatDao.getAllChatsByUserId(userId);
        var distinctSenderIds = chats.stream().map(Chat::getSenderId).distinct().toList();
        var chatGroups = new ArrayList<ChatGroup>();

        distinctSenderIds.forEach(senderId -> {
            var senderDetails = userDetailDao.findByUserId(senderId);

            var chatMessages = chats.stream()
                    .filter(chat -> chat.getSenderId().equals(senderId))
                    .map(chat -> ChatMessage.builder().type(chat.getMessage()).message(chat.getMessage())
                            .createdAt(chat.getCreatedAt()).build())
                    .sorted(Comparator.comparing(ChatMessage::getCreatedAt).reversed())
                    .toList();

            var chatGroup = ChatGroup.builder().senderId(senderId).senderFirstName(senderDetails.getFirstName())
                    .senderLastName(senderDetails.getLastName()).receiverId(userId).chats(chatMessages)
                    .senderProfileImage(senderDetails.getProfileImage()).build();

            chatGroups.add(chatGroup);
        });
        return chatGroups.stream()
                .filter(chatGroup -> !chatGroup.getChats().isEmpty())
                .sorted(Comparator.comparing(chatGroup -> chatGroup.getChats().get(0).getCreatedAt(),
                        Comparator.reverseOrder())).toList();
    }

    private UserDto getUserDetails(User user) {
        var userDetail = userDetailDao.findByUserId(user.getId().toString());
        return UserDto.builder().userId(user.getId().toString()).email(user.getEmail())
                .username(user.getUsername()).firstName(userDetail.getFirstName())
                .lastName(userDetail.getLastName()).profileImage(userDetail.getProfileImage()).build();
    }
}
