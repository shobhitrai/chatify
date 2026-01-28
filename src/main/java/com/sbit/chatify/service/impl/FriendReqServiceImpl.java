package com.sbit.chatify.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.SocketConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.dao.*;
import com.sbit.chatify.entity.*;
import com.sbit.chatify.model.*;
import com.sbit.chatify.service.FriendReqService;
import com.sbit.chatify.service.NotificationService;
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

    @Autowired
    private NotificationService notificationService;

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
                    .receiverId(friendRequestDto.getReceiverId()).isAccepted(false).isCanceled(false)
                    .isActive(true).message(friendRequestDto.getMessage()).createdAt(new Date()).build();
            friendRequestDao.save(friendRequest);

            //to sender
            var socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.SUCCESS_CODE)
                    .message(MessageConstant.SUCCESS).type(SocketConstant.ACK_FRIEND_REQUEST).build();
            SocketUtil.send(socketResponse);

            //to receiver
            UserDetail senderDetail = userDetailDao.findByUserId(userId);
            if (SocketUtil.isUserConnected(friendRequest.getReceiverId())) {
                ChatGroup chatGroup = getChatDto(friendRequest, senderDetail, friendRequest.getReceiverId());
                socketResponse = SocketResponse.builder().userId(friendRequestDto.getReceiverId())
                        .status(StatusConstant.SUCCESS_CODE).message(MessageConstant.SUCCESS)
                        .type(SocketConstant.CREATE_CHAT_GROUP).data(chatGroup).build();
                SocketUtil.send(socketResponse);
            }
            notificationService.sendNotification(senderDetail, friendRequestDto.getReceiverId(),
                    senderDetail.getFirstName() + MessageConstant.FRIEND_REQUEST_MESSAGE);
        } catch (Exception e) {
            log.info("Error while sending friend request: {}", e.getMessage());
            var socketResponse = SocketResponse.builder().userId(userId)
                    .status(StatusConstant.INTERNAL_SERVER_ERROR_CODE)
                    .message(MessageConstant.INTERNAL_SERVER_ERROR)
                    .type(SocketConstant.ACK_FRIEND_REQUEST).build();
            SocketUtil.send(socketResponse);
        }
    }

    private ChatGroup getChatDto(FriendRequest friendRequest, UserDetail senderDetail, String receiverId) {
        var chatMessage = ChatMessage.builder().type(MessageConstant.FRIEND_REQUEST)
                .message(friendRequest.getMessage()).createdAt(friendRequest.getCreatedAt())
                .formattedDate(Util.getChatFormatedDate(friendRequest.getCreatedAt())).build();

        return ChatGroup.builder().senderId(senderDetail.getUserId())
                .senderFirstName(senderDetail.getFirstName()).receiverId(receiverId)
                .senderLastName(senderDetail.getLastName()).chats(List.of(chatMessage))
                .senderProfileImage(senderDetail.getProfileImage()).build();
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
            FriendRequest friendRequest = friendRequestDao.findBySenderIdAndReceiverId(friendRequestDto.getSenderId(), userId);
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
            //for receiver
            var receiverContact = saveContact(userId, friendRequestDto.getSenderId());
            //for sender
            var senderContact = saveContact(friendRequestDto.getSenderId(), userId);

            // to sender
            socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.SUCCESS_CODE)
                    .message(MessageConstant.SUCCESS).type(SocketConstant.ACK_ACCEPT_FRIEND_REQUEST)
                    .data(receiverContact).build();
            SocketUtil.send(socketResponse);

            // to receiver
            socketResponse = SocketResponse.builder().userId(friendRequestDto.getSenderId())
                    .status(StatusConstant.SUCCESS_CODE).message(MessageConstant.SUCCESS)
                    .type(SocketConstant.ADD_CONTACT).data(senderContact).build();
            SocketUtil.send(socketResponse);

            String message = receiverContact.getFirstName() + MessageConstant.ACCEPT_FRIEND_REQUEST;
            UserDetail senderDetail = userDetailDao.findByUserId(userId);
            notificationService.sendNotification(senderDetail, friendRequestDto.getSenderId(), message);

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
        try {
            FriendRequest friendRequest = friendRequestDao.findBySenderIdAndReceiverId(friendRequestDto.getSenderId(), userId);
            if (Objects.isNull(friendRequest))
                return;

            friendRequest.setIsActive(false);
            friendRequestDao.save(friendRequest);

            if (SocketUtil.isUserConnected(friendRequestDto.getSenderId())) {
                var socketResponse = SocketResponse.builder().userId(friendRequestDto.getSenderId())
                        .status(StatusConstant.SUCCESS_CODE).message(MessageConstant.SUCCESS)
                        .type(SocketConstant.REMOVE_CONTACT).data(userId).build();
                SocketUtil.send(socketResponse);
            }

            UserDetail senderDetail = userDetailDao.findByUserId(userId);
            String message = senderDetail.getFirstName() + MessageConstant.REJECTED_FRIEND_REQUEST;
            notificationService.sendNotification(senderDetail, friendRequestDto.getSenderId(), message);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while rejecting friend request: {}", e.getMessage());
        }
    }

    @Override
    public void cancelFriendRequest(String userId, FriendRequestDto friendRequestDto) {
        try {
            FriendRequest friendRequest = friendRequestDao.findBySenderIdAndReceiverId(userId, friendRequestDto.getReceiverId());
            if (Objects.isNull(friendRequest))
                return;

            friendRequest.setIsActive(false);
            friendRequest.setIsCanceled(false);
            friendRequestDao.save(friendRequest);

            if (SocketUtil.isUserConnected(friendRequestDto.getReceiverId())) {
                var socketResponse = SocketResponse.builder().userId(friendRequestDto.getReceiverId())
                        .status(StatusConstant.SUCCESS_CODE).message(MessageConstant.SUCCESS)
                        .type(SocketConstant.REMOVE_CONTACT).data(userId).build();
                SocketUtil.send(socketResponse);
            }

            UserDetail senderDetail = userDetailDao.findByUserId(userId);
            String message = senderDetail.getFirstName() + " " + MessageConstant.CANCELED_FRIEND_REQUEST;
            notificationService.sendNotification(senderDetail, friendRequestDto.getReceiverId(), message);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while canceling friend request: {}", e.getMessage());
        }
    }


    private ContactDto saveContact(String userId, String userId2) {
        Contact contact = contactDao.findByUserId(userId);
        if (Objects.isNull(contact))
            contact = Contact.builder().userId(userId).contacts(new ArrayList<>()).build();

        UserDetail userDetails = userDetailDao.findByUserId(userId2);
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
