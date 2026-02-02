package com.sbit.chatify.service.impl;

import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.SocketConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.dao.ContactDao;
import com.sbit.chatify.dao.FriendRequestDao;
import com.sbit.chatify.dao.UserDetailDao;
import com.sbit.chatify.entity.Contact;
import com.sbit.chatify.entity.FriendRequest;
import com.sbit.chatify.entity.UserDetail;
import com.sbit.chatify.model.*;
import com.sbit.chatify.service.ChatService;
import com.sbit.chatify.utility.Util;
import com.sbit.chatify.websocket.SocketUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private FriendRequestDao friendRequestDao;

    @Autowired
    private UserDetailDao userDetailDao;

    @Override
    public void seenLastMsg(String userId, ContactDto contactDto) {
        contactDao.seenLastMsg(userId, contactDto.getContactId());
    }

    @Override
    public void getChat(String userId, ContactDto contactDto) {
        SocketResponse socketResponse = null;
        try {
            Contact contact = contactDao.findByUserId(userId);
            List<ChatDto> chats = new ArrayList<>();
            boolean isFriend = contact.getContacts().stream()
                    .anyMatch(c -> c.getContactId().equals(contactDto.getContactId()));
            if (!isFriend) {
                FriendRequest friendRequest = friendRequestDao.findActivePendingRequest(userId, contactDto.getContactId());
                if (friendRequest == null) {
                    if (SocketUtil.isUserConnected(userId)) {
                        socketResponse = SocketResponse.builder().userId(userId)
                                .status(StatusConstant.FAILURE_CODE).message(MessageConstant.NO_CHAT_FOUND)
                                .type(SocketConstant.CREATE_CHAT_GROUP).build();
                        SocketUtil.send(socketResponse);
                        return;
                    }
                }
                boolean isSender = userId.equals(friendRequest.getSenderId());
                String otherUserId = isSender ? userId : contactDto.getContactId();
                UserDetail userDetail = userDetailDao.findByUserId(otherUserId);

                ChatDto chatDto = ChatDto.builder()
                        .type(isSender ? MessageConstant.SENT_FRIEND_REQUEST
                                : MessageConstant.RECEIVED_FRIEND_REQUEST)
                        .message(isSender ? MessageConstant.SENT_FRIEND_REQUEST_MESSAGE
                                + userDetail.getFirstName() + " " + userDetail.getLastName()
                                : friendRequest.getMessage())
                        .createdAt(friendRequest.getCreatedAt())
                        .formattedDate(Util.getChatFormatedDate(friendRequest.getCreatedAt()))
                        .build();
                chats.add(chatDto);
            }
            socketResponse = SocketResponse.builder().userId(userId)
                    .status(StatusConstant.FAILURE_CODE).message(MessageConstant.NO_CHAT_FOUND)
                    .type(SocketConstant.CREATE_CHAT_GROUP).build();
            SocketUtil.send(socketResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
