package com.sbit.chatify.service.impl;

import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.PageConstant;
import com.sbit.chatify.dao.*;
import com.sbit.chatify.entity.*;
import com.sbit.chatify.model.*;
import com.sbit.chatify.service.WallService;
import com.sbit.chatify.utility.Util;
import com.sbit.chatify.websocket.SocketUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.*;

@Service
@Slf4j
public class WallServiceImpl implements WallService {

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
    public String getWallData(Model model, HttpSession session) {
        String userId = (String) session.getAttribute(MessageConstant.USER_ID);
        if (Objects.isNull(userId) || SocketUtil.isUserConnected(userId))
            return PageConstant.REDIRECT_LOGIN;

        User user = userDao.findById(new ObjectId(userId));
        UserDto userDto = getUserDetails(user);
        List<ChatGroup> chats = getAllChats(userId);
        List<NotificationDto> notifications = getAllNotifications(userId);
        List<ContactDto> contacts = getAllContacts(userId);

        model.addAttribute(MessageConstant.USER, userDto);
        model.addAttribute(MessageConstant.CONTACTS, contacts);
        model.addAttribute(MessageConstant.CHAT_GROUPS, chats);
        model.addAttribute(MessageConstant.NOTIFICATIONS, notifications);
        return PageConstant.WALL;
    }

    private List<ContactDto> getAllContacts(String userId) {
        Contact contacts = contactDao.findByUserId(userId);
        if (Objects.isNull(contacts))
            return Collections.emptyList();
        List<ContactDto> contactDtos = new ArrayList<>();
        contacts.getContacts().forEach(contact -> {
            UserDetail userDetail = userDetailDao.findByUserId(contact.getContactId());
            boolean isOnline = SocketUtil.isUserConnected(contact.getContactId());
            ContactDto contactDto = ContactDto.builder().contactId(contact.getContactId())
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
        List<Notification> notifications = notificationDao.findByReceiverId(userId);
        if (notifications.isEmpty())
            return Collections.emptyList();
        return notifications.stream().map(notification -> {
            UserDetail senderDetails = userDetailDao.findByUserId(notification.getSenderId());
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
        List<FriendRequest> friendRequests = friendRequestDao.findActivePendingRequest(userId);

        return friendRequests.stream().map(fr -> {
            boolean isSender = userId.equals(fr.getSenderId());
            String otherUserId = isSender ? fr.getReceiverId() : fr.getSenderId();
            UserDetail userDetail = userDetailDao.findByUserId(otherUserId);

            ChatMessage chatMessage = ChatMessage.builder()
                    .type(isSender ? MessageConstant.SENT_FRIEND_REQUEST
                            : MessageConstant.RECEIVED_FRIEND_REQUEST)
                    .message(isSender ? MessageConstant.SENT_FRIEND_REQUEST_MESSAGE
                            + userDetail.getFirstName() + " " + userDetail.getLastName()
                            : fr.getMessage())
                    .createdAt(fr.getCreatedAt())
                    .formattedDate(Util.getChatFormatedDate(fr.getCreatedAt()))
                    .build();

            return ChatGroup.builder()
                    .senderId(otherUserId)
                    .senderFirstName(userDetail.getFirstName())
                    .senderLastName(userDetail.getLastName())
                    .receiverId(userId)
                    .senderProfileImage(userDetail.getProfileImage())
                    .chats(List.of(chatMessage))
                    .isSenderOnline(SocketUtil.isUserConnected(otherUserId))
                    .build();
        }).sorted(Comparator.comparing(chatGroup -> chatGroup.getChats().get(0).getCreatedAt(),
                Comparator.reverseOrder())).toList();
    }

    private UserDto getUserDetails(User user) {
        var userDetail = userDetailDao.findByUserId(user.getId().toString());
        return UserDto.builder().userId(user.getId().toString()).email(user.getEmail())
                .username(user.getUsername()).firstName(userDetail.getFirstName())
                .lastName(userDetail.getLastName()).profileImage(userDetail.getProfileImage()).build();
    }
}
