package com.sbit.chatify.service.impl;

import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.PageConstant;
import com.sbit.chatify.dao.ChatDao;
import com.sbit.chatify.dao.UserDao;
import com.sbit.chatify.dao.UserDetailDao;
import com.sbit.chatify.entity.Chat;
import com.sbit.chatify.entity.User;
import com.sbit.chatify.model.ChatDto;
import com.sbit.chatify.model.UserDto;
import com.sbit.chatify.service.WallService;
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

    @Override
    public String getWallData(Model model) {
        var userId = (String) session.getAttribute(MessageConstant.USER_ID);
        if (Objects.isNull(userId) || SocketUtil.SOCKET_CONNECTIONS.containsKey(userId))
            return PageConstant.REDIRECT_LOGIN;

        var user = userDao.findById(new ObjectId(userId));
        var userDto = getUserDetails(user);
        var chats = getAllLatestChat(userId);

        model.addAttribute(MessageConstant.USER, userDto);
        model.addAttribute(MessageConstant.CHATS, chats);
        return PageConstant.WALL;
    }

    private List<ChatDto> getAllLatestChat(String userId) {
        var chats = chatDao.getAllChatsByUserId(userId);
        var distinctSenderIds = chats.stream().map(Chat::getSenderId).distinct().toList();
        var chatList = new ArrayList<ChatDto>();

        distinctSenderIds.forEach(senderId -> {
            var senderDetails = userDetailDao.findByUserId(senderId);
            var chatDto = chats.stream()
                    .filter(chat -> chat.getSenderId().equals(senderId))
                    .map(chat -> ChatDto.builder().type(chat.getType()).message(chat.getMessage())
                            .createdAt(chat.getCreatedAt()).isRead(chat.getIsRead())
                            .senderId(userId).senderFirstName(senderDetails.getFirstName())
                            .senderLastName(senderDetails.getLastName()).receiverId(userId)
                            .senderProfileImage(senderDetails.getProfileImage()).build())
                    .findFirst().orElse(null);
            chatList.add(chatDto);
        });
        return chatList.stream()
                .sorted(Comparator.comparing(ChatDto::getCreatedAt).reversed()).toList();
    }

    private UserDto getUserDetails(User user) {
        var userDetail = userDetailDao.findByUserId(user.getId().toString());
        return UserDto.builder().userId(user.getId().toString()).email(user.getEmail())
                .username(user.getUsername()).firstName(userDetail.getFirstName())
                .lastName(userDetail.getLastName()).profileImage(userDetail.getProfileImage()).build();
    }
}
