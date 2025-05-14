package com.sbit.chatify.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.SocketConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.dao.*;
import com.sbit.chatify.entity.*;
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

    @Autowired
    private ContactDao contactDao;

    @Override
    public void sendFriendRequest(String userId, FriendRequestDto friendRequestDto) {
        try {
            var contact = contactDao.findByUserId(userId);
            if (Objects.nonNull(contact) && contact.getContacts().stream()
                    .anyMatch(contactInfo ->
                            contactInfo.getContactId().equals(friendRequestDto.getReceiverId()))) {
                var socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.FAILURE_CODE)
                        .message(MessageConstant.ALREADY_CONTACT)
                        .type(SocketConstant.ACK_FRIEND_REQUEST).build();
                SocketUtil.send(socketResponse);
                return;
            }
            // Friend req already exists either by sender or receiver
            var isReqAlreadyExist = friendRequestDao.isFriendRequestExist(userId,
                    friendRequestDto.getReceiverId());

            if (isReqAlreadyExist) {
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

            if (SocketUtil.isUserConnected(receiverId)) {
                var notificationDto = getNotificationDto(notification, senderDetail, receiverId);
                var chatGroup = getChatDto(chat, senderDetail, receiverId);
                Map<String, Object> data = new HashMap<>();
                data.put(MessageConstant.NOTIFICATIONS, notificationDto);
                data.put(MessageConstant.CHAT_GROUPS, chatGroup);
                var socketResponse = SocketResponse.builder().userId(receiverId)
                        .status(StatusConstant.SUCCESS_CODE).message(MessageConstant.SUCCESS)
                        .type(SocketConstant.CREATE_CHAT_GROUP).data(data).build();
                SocketUtil.send(socketResponse);
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
                .receiverId(receiverId).isRecent(Util.isRecent(notification.getCreatedAt()))
                .formattedDate(Util.getNotificationFormatedDate(notification.getCreatedAt()))
                .senderProfileImage(senderDetail.getProfileImage())
                .senderFirstName(senderDetail.getFirstName())
                .senderLastName(senderDetail.getLastName()).build();
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

    @Override
    public void acceptFriendRequest(String userId, FriendRequestDto friendRequestDto) {
        SocketResponse socketResponse = null;
        try {
            var friendRequest = friendRequestDao.findBySenderIdAndReceiverId(friendRequestDto.getSenderId(), userId);
            if (Objects.isNull(friendRequest)) {
                socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.FAILURE_CODE)
                        .message(MessageConstant.INVALID_USER)
                        .type(SocketConstant.ACK_ACCEPT_FRIEND_REQUEST).build();
                SocketUtil.send(socketResponse);
                return;
            }
            friendRequest.setIsAccepted(true);
            friendRequest.setIsActive(false);
            friendRequestDao.save(friendRequest);
            chatDao.inactiveFriendRequestMsg(friendRequest.getSenderId(), userId);

            //for receiver
            var receiverContact = saveContact(userId, friendRequestDto.getSenderId());
            //for sender
            var senderContact = saveContact(friendRequestDto.getSenderId(), userId);

            socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.SUCCESS_CODE)
                    .message(MessageConstant.SUCCESS).type(SocketConstant.ACK_ACCEPT_FRIEND_REQUEST)
                    .data(receiverContact).build();
            SocketUtil.send(socketResponse);

            notifyToSender(friendRequestDto.getSenderId(), senderContact);
        } catch (Exception e) {
            log.error("Error while accepting friend request: {}", e.getMessage());
            socketResponse = SocketResponse.builder().userId(userId)
                    .status(StatusConstant.INTERNAL_SERVER_ERROR_CODE)
                    .message(MessageConstant.INTERNAL_SERVER_ERROR)
                    .type(SocketConstant.ACK_ACCEPT_FRIEND_REQUEST).build();
            SocketUtil.send(socketResponse);
        }
    }

    @Override
    public void rejectFriendRequest(String userId, FriendRequestDto friendRequestDto) {
        SocketResponse socketResponse = null;
        try {
            var friendRequest = friendRequestDao.findBySenderIdAndReceiverId(friendRequestDto.getSenderId(), userId);
            if (Objects.isNull(friendRequest)) {
                socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.FAILURE_CODE)
                        .message(MessageConstant.INVALID_USER)
                        .type(SocketConstant.ACK_REJECT_FRIEND_REQUEST).build();
                SocketUtil.send(socketResponse);
                return;
            }

            friendRequest.setIsActive(false);
            friendRequestDao.save(friendRequest);
            chatDao.inactiveFriendRequestMsg(friendRequest.getSenderId(), userId);

            socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.SUCCESS_CODE)
                    .message(MessageConstant.SUCCESS).type(SocketConstant.ACK_REJECT_FRIEND_REQUEST)
                    .build();
            SocketUtil.send(socketResponse);

            notifyToSender(friendRequestDto.getSenderId(), userId);

        } catch (Exception e) {
            log.error("Error while rejecting friend request: {}", e.getMessage());
            socketResponse = SocketResponse.builder().userId(userId)
                    .status(StatusConstant.INTERNAL_SERVER_ERROR_CODE)
                    .message(MessageConstant.INTERNAL_SERVER_ERROR)
                    .type(SocketConstant.ACK_REJECT_FRIEND_REQUEST).build();
            SocketUtil.send(socketResponse);
        }
    }

    private void notifyToSender(String senderId, String userId) {
        try {
            UserDetail userDetail = userDetailDao.findByUserId(userId);
            Notification notification = Notification.builder()
                    .senderId(userId).receiverId(senderId)
                    .message(userDetail.getFirstName() + " " + MessageConstant.REJECTED_FRIEND_REQUEST)
                    .createdAt(new Date()).isRead(false).build();
            notificationDao.save(notification);
            if (SocketUtil.isUserConnected(senderId)) {
                var notificationDto = NotificationDto.builder().createdAt(notification.getCreatedAt())
                        .message(notification.getMessage()).senderId(notification.getSenderId())
                        .receiverId(senderId).isRecent(Util.isRecent(notification.getCreatedAt()))
                        .formattedDate(Util.getNotificationFormatedDate(notification.getCreatedAt()))
                        .senderProfileImage(userDetail.getProfileImage())
                        .senderFirstName(userDetail.getFirstName())
                        .senderLastName(userDetail.getLastName()).build();

                var socketResponse = SocketResponse.builder().userId(senderId)
                        .status(StatusConstant.SUCCESS_CODE).message(MessageConstant.SUCCESS)
                        .type(SocketConstant.REMOVE_CONTACT).data(notificationDto).build();
                SocketUtil.send(socketResponse);
            }
        } catch (Exception e) {
            log.error("Error while sending remove contact notification: {}", e.getMessage());
        }
    }

    private void notifyToSender(String receiverId, ContactDto senderContact) {
        try {
            Notification notification = Notification.builder()
                    .senderId(senderContact.getContactId()).receiverId(receiverId)
                    .message(senderContact.getFirstName() + " " + MessageConstant.ACCEPT_FRIEND_REQUEST)
                    .createdAt(new Date()).isRead(false).build();
            notificationDao.save(notification);
            if (SocketUtil.isUserConnected(receiverId)) {
                var notificationDto = NotificationDto.builder().createdAt(notification.getCreatedAt())
                        .message(notification.getMessage()).senderId(notification.getSenderId())
                        .receiverId(receiverId).isRecent(Util.isRecent(notification.getCreatedAt()))
                        .formattedDate(Util.getNotificationFormatedDate(notification.getCreatedAt()))
                        .senderProfileImage(senderContact.getProfileImage())
                        .senderFirstName(senderContact.getFirstName())
                        .senderLastName(senderContact.getLastName()).build();
                Map<String, Object> data = new HashMap<>();
                data.put(MessageConstant.NOTIFICATIONS, notificationDto);
                data.put(MessageConstant.CONTACTS, senderContact);
                var socketResponse = SocketResponse.builder().userId(receiverId)
                        .status(StatusConstant.SUCCESS_CODE).message(MessageConstant.SUCCESS)
                        .type(SocketConstant.ADD_CONTACT).data(data).build();
                SocketUtil.send(socketResponse);
            }
        } catch (Exception e) {
            log.error("Error while sending add contact notification: {}", e.getMessage());
        }
    }

    private ContactDto saveContact(String userId, String userId2) {
        var contact = contactDao.findByUserId(userId);
        if (Objects.isNull(contact))
            contact = Contact.builder().userId(userId).contacts(new ArrayList<>()).build();

        var userDetails = userDetailDao.findByUserId(userId2);
        ContactInfo contactInfo = ContactInfo.builder()
                .contactId(userId2).contactFirstName(userDetails.getFirstName())
                .contactLastName(userDetails.getLastName()).createdAt(new Date())
                .isLastMsgSeen(false).unreadMsgCount(0).build();
        contact.getContacts().add(contactInfo);
        contactDao.save(contact);
        return ContactDto.builder().contactId(userDetails.getUserId())
                .lastName(userDetails.getLastName())
                .firstName(userDetails.getFirstName())
                .profileImage(userDetails.getProfileImage())
                .createdAt(contactInfo.getCreatedAt()).userId(userId)
                .isOnline(SocketUtil.isUserConnected(userDetails.getUserId())).build();
    }

}
