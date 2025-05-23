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

import java.util.*;

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

    @Autowired
    private ContactDao contactDao;

    @Override
    public String getWallData(Model model) {
        var userId = (String) session.getAttribute(MessageConstant.USER_ID);
        if (Objects.isNull(userId) || SocketUtil.isUserConnected(userId))
            return PageConstant.REDIRECT_LOGIN;

        User user = userDao.findById(new ObjectId(userId));
        UserDto userDto = getUserDetails(user);
        List<ChatGroup> chats = getAllChats(userId);
        List<NotificationDto> notifications = getAllNotifications(userId);
        List<ContactDto> contacts = getAllcontacts(userId);

        model.addAttribute(MessageConstant.USER, userDto);
        model.addAttribute(MessageConstant.CONTACTS, contacts);
        model.addAttribute(MessageConstant.CHAT_GROUPS, chats);
        model.addAttribute(MessageConstant.NOTIFICATIONS, notifications);
        return PageConstant.WALL;
    }

    private List<ContactDto> getAllcontacts(String userId) {
        var contacts = contactDao.findByUserId(userId);
        if (Objects.isNull(contacts))
            return Collections.emptyList();
        var contactDtos = new ArrayList<ContactDto>();
        contacts.getContacts().forEach(contact -> {
            var userDetail = userDetailDao.findByUserId(contact.getContactId());
            boolean isOnline = SocketUtil.isUserConnected(contact.getContactId());
            var contactDto = ContactDto.builder().contactId(contact.getContactId())
                    .firstName(userDetail.getFirstName()).lastName(userDetail.getLastName()).userId(userId)
                    .createdAt(contact.getCreatedAt()).profileImage(userDetail.getProfileImage())
                    .isOnline(isOnline).build();
            contactDtos.add(contactDto);
        });
        return contactDtos.stream()
                .sorted(Comparator.comparing(ContactDto::getCreatedAt, Comparator.reverseOrder()))
                .toList();
    }

    private List<NotificationDto> getAllNotifications(String userId) {
        var notifications = notificationDao.findByReceiverId(userId);
        if (notifications.isEmpty())
            return Collections.emptyList();
        return notifications.stream().map(notification -> {
            var senderDetails = userDetailDao.findByUserId(notification.getSenderId());
            return NotificationDto.builder().createdAt(notification.getCreatedAt())
                    .message(notification.getMessage()).senderId(notification.getSenderId())
                    .receiverId(userId).isRecent(Util.isRecent(notification.getCreatedAt()))
                    .formattedDate(Util.getNotificationFormatedDate(notification.getCreatedAt()))
                    .senderProfileImage(senderDetails.getProfileImage())
                    .senderFirstName(senderDetails.getFirstName())
                    .senderLastName(senderDetails.getLastName())
                    .isUserOnline(SocketUtil.isUserConnected(senderDetails.getUserId())).build();
        }).toList();
    }

    private List<ChatGroup> getAllChats(String userId) {
        var chats = chatDao.getAllChatsByUserId(userId);
        if (chats.isEmpty())
            return Collections.emptyList();
        var distinctSenderIds = chats.stream().map(Chat::getSenderId).distinct().toList();
        var chatGroups = new ArrayList<ChatGroup>();

        distinctSenderIds.forEach(senderId -> {
            var senderDetails = userDetailDao.findByUserId(senderId);
            var chatMessages = chats.stream()
                    .filter(chat -> chat.getSenderId().equals(senderId))
                    .map(chat -> ChatMessage.builder().type(chat.getType())
                            .message(chat.getMessage()).createdAt(chat.getCreatedAt())
                            .formattedDate(Util.getChatFormatedDate(chat.getCreatedAt()))
                            .build())
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
