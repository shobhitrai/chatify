package com.sbit.chatify.service.impl;

import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.SocketConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.dao.ChatDao;
import com.sbit.chatify.dao.ContactDao;
import com.sbit.chatify.dao.FriendRequestDao;
import com.sbit.chatify.dao.UserDetailDao;
import com.sbit.chatify.entity.Chat;
import com.sbit.chatify.entity.Contact;
import com.sbit.chatify.entity.FriendRequest;
import com.sbit.chatify.entity.UserDetail;
import com.sbit.chatify.model.*;
import com.sbit.chatify.service.ChatService;
import com.sbit.chatify.utility.Util;
import com.sbit.chatify.websocket.SocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
            socketResponse = isFriend(userId, contactId)
                    ? getChatsBetweenUsers(userId, contactId)
                    : friendRequestChat(userId, contactId);

        } catch (Exception e) {
            log.error("Error in getChat: {}", e.getMessage());
            socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.INTERNAL_SERVER_ERROR_CODE)
                    .message(MessageConstant.INTERNAL_SERVER_ERROR).type(SocketConstant.ACK_GET_CHAT).build();
        } finally {
            SocketUtil.send(socketResponse);
        }
    }

    private boolean isFriend(String userId, String contactId) {
        return Optional.ofNullable(contactDao.findByUserId(userId))
                .map(Contact::getContacts).orElse(Collections.emptyList())
                .stream().anyMatch(c -> c.getContactId().equals(contactId));
    }

    private SocketResponse getChatsBetweenUsers(String userId, String contactId) {
        List<ChatDto> chats = chatDao.findChatBySenderAndReceiverId(userId, contactId)
                .stream().map(this::mapToChatDto).toList();
        Map<String, Object> data = buildData(contactId, chats);
        return SocketResponse.builder().userId(userId).status(StatusConstant.SUCCESS_CODE).data(data)
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

        Map<String, Object> data = buildData(contactId, List.of(chatDto));
        return SocketResponse.builder().userId(userId).status(StatusConstant.SUCCESS_CODE).data(data)
                .type(SocketConstant.ACK_GET_CHAT).build();
    }

    private Map<String, Object> buildData(String contactId, List<ChatDto> chats) {
        Map<String, Object> data = new HashMap<>();
        data.put("otherUser", buildUserDto(contactId));
        data.put("chat", chats);
        return data;
    }

    private Object buildUserDto(String contactId) {
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
                .formattedDate(Util.getChatFormatedDate(chat.getCreatedAt()))
                .build();
    }
}
