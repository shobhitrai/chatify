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
import java.util.function.Function;
import java.util.stream.Collectors;

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

        List<ChatGroup> chats = new ArrayList<>(getAllFriendRequest(userId));
        chats.addAll(getLatestContactsChat(userId));
        List<NotificationDto> notifications = getAllNotifications(userId);
        List<ContactDto> contacts = getAllContacts(userId);

        model.addAttribute(MessageConstant.USER, userDto);
        model.addAttribute(MessageConstant.CONTACTS, contacts);
        model.addAttribute(MessageConstant.CHAT_GROUPS, chats);
        model.addAttribute(MessageConstant.NOTIFICATIONS, notifications);
        return PageConstant.WALL;
    }

    private List<ChatGroup> getLatestContactsChat(String userId) {
        List<Contact> contacts = contactDao.findByUserId(userId);
        if (contacts.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Chat> latestChatByContact =
                chatDao.findLatestChatsForAllContact(userId).stream()
                        .collect(Collectors.toMap(
                                chat -> chat.getSenderId().equals(userId)
                                        ? chat.getReceiverId()
                                        : chat.getSenderId(),
                                Function.identity()
                        ));

        return contacts.stream().map(contact -> {
                    Chat chat = latestChatByContact.get(contact.getContactId());
                    if (chat == null) return null;

                    ChatDto chatDto = ChatDto.builder()
                            .senderId(chat.getSenderId())
                            .receiverId(chat.getReceiverId())
                            .type(chat.getType())
                            .message(chat.getMessage())
                            .createdAt(chat.getCreatedAt())
                            .formattedDate(Util.getChatFormatedDate(chat.getCreatedAt()))
                            .build();

                    return ChatGroup.builder()
                            .contact(buildUserDto(contact.getContactId(), contact.getUnreadMsgCount()))
                            .chats(List.of(chatDto))
                            .build();
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(
                        (ChatGroup cg) -> cg.getChats().get(0).getCreatedAt()).reversed())
                .toList();
    }

    private List<ChatGroup> getAllFriendRequest(String userId) {
        List<FriendRequest> friendRequests = friendRequestDao.findActivePendingRequest(userId);
        if (friendRequests.isEmpty()) return Collections.emptyList();

        return friendRequests.stream().map(friendRequest -> {
            boolean isSender = userId.equals(friendRequest.getSenderId());
            String contactId = isSender ? friendRequest.getReceiverId() : friendRequest.getSenderId();
            ChatDto chatDto = ChatDto.builder()
                    .senderId(friendRequest.getSenderId())
                    .receiverId(friendRequest.getReceiverId())
                    .type(MessageConstant.FRIEND_REQUEST)
                    .message(isSender ? MessageConstant.SENT_FRIEND_REQUEST_MESSAGE : friendRequest.getMessage())
                    .createdAt(friendRequest.getCreatedAt())
                    .formattedDate(Util.getChatFormatedDate(friendRequest.getCreatedAt()))
                    .build();
            return ChatGroup.builder().contact(buildUserDto(contactId, null))
                    .chats(List.of(chatDto)).build();
        }).toList();
    }

    private UserDto buildUserDto(String contactId, Integer unreadMsgCount) {
        UserDetail userDetail = userDetailDao.findByUserId(contactId);
        return UserDto.builder()
                .userId(userDetail.getUserId())
                .firstName(userDetail.getFirstName())
                .lastName(userDetail.getLastName())
                .profileImage(userDetail.getProfileImage())
                .isOnline(SocketUtil.isUserConnected(contactId))
                .unreadMsgCount(unreadMsgCount)
                .build();
    }

    private List<ContactDto> getAllContacts(String userId) {
        List<Contact> contacts = contactDao.findByUserId(userId);
        if (Objects.isNull(contacts))
            return Collections.emptyList();

        return contacts.stream().map(contact -> {
                    UserDetail userDetail = userDetailDao.findByUserId(contact.getContactId());
                    return ContactDto.builder()
                            .contactId(contact.getContactId())
                            .firstName(userDetail.getFirstName())
                            .lastName(userDetail.getLastName())
                            .userId(userId)
                            .createdAt(contact.getCreatedAt())
                            .profileImage(userDetail.getProfileImage())
                            .isOnline(SocketUtil.isUserConnected(contact.getContactId()))
                            .build();
                })
                .sorted(Comparator.comparing(ContactDto::getCreatedAt, Comparator.reverseOrder()))
                .toList();
    }

    private List<NotificationDto> getAllNotifications(String userId) {
        List<Notification> notifications = notificationDao.findByReceiverId(userId);
        if (notifications.isEmpty())
            return Collections.emptyList();

        return notifications.stream().map(notification -> {
            UserDetail senderDetails = userDetailDao.findByUserId(notification.getSenderId());
            return NotificationDto.builder()
                    .createdAt(notification.getCreatedAt())
                    .message(notification.getMessage())
                    .senderId(notification.getSenderId())
                    .receiverId(userId)
                    .isRecent(Util.isRecent(notification.getCreatedAt()))
                    .formattedDate(Util.getNotificationFormatedDate(notification.getCreatedAt()))
                    .senderProfileImage(senderDetails.getProfileImage())
                    .senderFirstName(senderDetails.getFirstName())
                    .senderLastName(senderDetails.getLastName())
                    .isUserOnline(SocketUtil.isUserConnected(senderDetails.getUserId()))
                    .build();
        }).toList();
    }

    private UserDto getUserDetails(User user) {
        var userDetail = userDetailDao.findByUserId(user.getId().toString());
        return UserDto.builder()
                .userId(user.getId().toString())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(userDetail.getFirstName())
                .lastName(userDetail.getLastName())
                .profileImage(userDetail.getProfileImage())
                .build();
    }
}
