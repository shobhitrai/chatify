package com.sbit.chatify.service.impl;

import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.SocketConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.dao.ChatDao;
import com.sbit.chatify.dao.ContactDao;
import com.sbit.chatify.dao.FriendRequestDao;
import com.sbit.chatify.dao.UserDetailDao;
import com.sbit.chatify.entity.Chat;
import com.sbit.chatify.entity.FriendRequest;
import com.sbit.chatify.entity.UserDetail;
import com.sbit.chatify.model.*;
import com.sbit.chatify.service.ChatService;
import com.sbit.chatify.utility.Util;
import com.sbit.chatify.websocket.SocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private FriendRequestDao friendRequestDao;

    @Autowired
    private UserDetailDao userDetailDao;

    @Autowired
    private ChatDao chatDao;

    @Override
    public void seenLastMsg(String userId, ContactDto contactDto) {
        contactDao.seenLastMsg(userId, contactDto.getContactId());
    }

    @Override
    public void getChat(String userId, ContactDto contactDto) {
        if (!SocketUtil.isUserConnected(userId))
            return;

        SocketResponse socketResponse = null;
        try {
            String contactId = contactDto.getContactId();
            boolean isFriend = isFriend(userId, contactId);
            socketResponse = isFriend ? getChatsBetweenUsers(userId, contactId) : friendRequestChat(userId, contactId);
            contactDao.resetUnseenMsg(userId, contactId);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in getChat: {}", e.getMessage());
            socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.INTERNAL_SERVER_ERROR_CODE)
                    .message(MessageConstant.INTERNAL_SERVER_ERROR).type(SocketConstant.ACK_GET_CHAT).build();
        } finally {
            SocketUtil.send(socketResponse);
        }
    }

    @Override
    public void sendTextMessage(String userId, ChatDto chatDto) {
        SocketResponse socketResponse = null;
        try {
            String message = chatDto.getMessage();
            String receiverId = chatDto.getReceiverId();

            if (message == null || receiverId == null || message.isBlank() || receiverId.isBlank())
                return;

            if (!isFriend(userId, receiverId))
                return;

            Date date = Util.getCurrentDateTime();
            ChatDto dataForReceiver = ChatDto.builder().senderId(userId).receiverId(receiverId)
                    .type(MessageConstant.TEXT).message(message).createdAt(date).build();

            if (SocketUtil.isUserConnected(receiverId)) {
                socketResponse = SocketResponse.builder().userId(receiverId).status(StatusConstant.SUCCESS_CODE)
                        .data(dataForReceiver).type(SocketConstant.RECEIVED_TEXT_MESSAGE).build();
                SocketUtil.send(socketResponse);
            } else {
                contactDao.incrementUnseenMsg(userId, receiverId);
            }

            Chat chat = Chat.builder().senderId(userId).receiverId(receiverId).type(MessageConstant.TEXT)
                    .message(message).createdAt(date).isActive(true).build();
            chatDao.save(chat);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in sendTextMessage: {}", e.getMessage());
            socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.INTERNAL_SERVER_ERROR_CODE)
                    .message(MessageConstant.SOMETHING_WENT_WRONG).type(SocketConstant.ACK_TEXT_MESSAGE).build();
            SocketUtil.send(socketResponse);
        }

    }

    private boolean isFriend(String userId, String contactId) {
        return contactDao.isFriend(userId, contactId);
    }

    private SocketResponse getChatsBetweenUsers(String userId, String contactId) {
        List<Chat> chats = chatDao.findChatBySenderAndReceiverId(userId, contactId);
        List<ChatDto> chatDtos = chats.stream().map(this::mapToChatDto).toList();
        ChatGroup chatGroup = ChatGroup.builder().contact(buildUserDto(contactId)).chats(chatDtos).build();
        return SocketResponse.builder().userId(userId).status(StatusConstant.SUCCESS_CODE).data(chatGroup)
                .type(SocketConstant.ACK_GET_CHAT).build();
    }

    private SocketResponse friendRequestChat(String userId, String contactId) {
        FriendRequest friendRequest = friendRequestDao.findActivePendingRequest(userId, contactId);

        if (friendRequest == null)
            return SocketResponse.builder().userId(userId)
                    .status(StatusConstant.FAILURE_CODE).message(MessageConstant.SOMETHING_WENT_WRONG)
                    .type(SocketConstant.ACK_GET_CHAT).build();

        boolean isSender = userId.equals(friendRequest.getSenderId());
        ChatDto chatDto = ChatDto.builder()
                .senderId(friendRequest.getSenderId())
                .receiverId(friendRequest.getReceiverId())
                .type(MessageConstant.FRIEND_REQUEST)
                .message(isSender ? MessageConstant.SENT_FRIEND_REQUEST_MESSAGE : friendRequest.getMessage())
                .createdAt(friendRequest.getCreatedAt())
                .formattedDate(Util.getChatFormatedDate(friendRequest.getCreatedAt()))
                .build();

        ChatGroup chatGroup = ChatGroup.builder().contact(buildUserDto(contactId))
                .chats(List.of(chatDto)).build();

        return SocketResponse.builder().userId(userId).status(StatusConstant.SUCCESS_CODE).data(chatGroup)
                .type(SocketConstant.ACK_GET_CHAT).build();
    }

    private UserDto buildUserDto(String contactId) {
        UserDetail userDetail = userDetailDao.findByUserId(contactId);
        return UserDto.builder()
                .userId(userDetail.getUserId())
                .firstName(userDetail.getFirstName())
                .lastName(userDetail.getLastName())
                .profileImage(userDetail.getProfileImage())
                .isOnline(SocketUtil.isUserConnected(contactId))
                .build();
    }

    private ChatDto mapToChatDto(Chat chat) {
        return ChatDto.builder()
                .senderId(chat.getSenderId())
                .receiverId(chat.getReceiverId())
                .type(chat.getType())
                .message(chat.getMessage())
                .createdAt(chat.getCreatedAt())
                .build();
    }
}
