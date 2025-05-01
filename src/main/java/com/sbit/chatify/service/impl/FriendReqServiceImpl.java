package com.sbit.chatify.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.SocketConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.dao.FriendRequestDao;
import com.sbit.chatify.dao.UserDao;
import com.sbit.chatify.dao.UserDetailDao;
import com.sbit.chatify.entity.FriendRequest;
import com.sbit.chatify.model.FriendRequestDto;
import com.sbit.chatify.model.Response;
import com.sbit.chatify.model.SocketResponse;
import com.sbit.chatify.model.UserDto;
import com.sbit.chatify.service.FriendReqService;
import com.sbit.chatify.utility.Util;
import com.sbit.chatify.websocket.SocketUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
public class FriendReqServiceImpl implements FriendReqService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private FriendRequestDao friendRequestDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserDetailDao userDetailDao;

    @Autowired
    private HttpSession session;

    @Override
    public void sendFriendRequest(String userId, FriendRequestDto friendRequestDto) {
        SocketResponse socketResponse = null;
        try {
            var friendReqAlredyExists = friendRequestDao.findBySenderIdAndReceiverId(userId,
                    friendRequestDto.getReceiverId());

            if (friendReqAlredyExists) {
                socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.FAILURE_CODE)
                        .message(MessageConstant.FRIEND_REQUEST_ALREADY_EXISTS)
                        .type(SocketConstant.ACK_FRIEND_REQUEST).build();
                return;
            }
            var friendRequest = FriendRequest.builder().senderId(userId)
                    .receiverId(friendRequestDto.getReceiverId()).isAccepted(false).createdAt(new Date()).build();
            friendRequestDao.save(friendRequest);

            socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.SUCCESS_CODE)
                    .message(MessageConstant.SUCCESS).type(SocketConstant.ACK_FRIEND_REQUEST).build();

        } catch (Exception e) {
            e.printStackTrace();
            socketResponse = Util.serverError(userId, SocketConstant.ACK_FRIEND_REQUEST);
        } finally {
            SocketUtil.send(socketResponse);
        }
    }

    @Override
    public ResponseEntity<Response> getSearchedUsers(UserDto userDto) {
        try {
            var users = userDao.findByUserName(userDto.getUsername());
            var foundUsers = new ArrayList<>();
            var userId = session.getAttribute("userId").toString();
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
                return Util.failure(MessageConstant.NO_USER_FOUND);
            else
                return Util.success(foundUsers);

        } catch (Exception e) {
            e.printStackTrace();
            return Util.serverError();
        }
    }

}
