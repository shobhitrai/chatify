package com.sbit.chatify.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.dao.FriendRequestDao;
import com.sbit.chatify.dao.UserDao;
import com.sbit.chatify.dao.UserDetailDao;
import com.sbit.chatify.entity.FriendRequest;
import com.sbit.chatify.model.FriendRequestDto;
import com.sbit.chatify.model.Response;
import com.sbit.chatify.model.UserDto;
import com.sbit.chatify.service.UserService;
import com.sbit.chatify.utility.Util;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

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
    public ResponseEntity<Response> sendFriendRequest(FriendRequestDto friendRequestDto) {
        try {
            var friendRequest = mapper.convertValue(friendRequestDto, FriendRequest.class);
            friendRequest.setCreatedAt(new Date());
            friendRequestDao.save(friendRequest);
            return ResponseEntity.ok(Response.builder()
                    .status(StatusConstant.SUCCESS_CODE)
                    .message(MessageConstant.SUCCESS).build());
        } catch (Exception e) {
            e.printStackTrace();
            return Util.serverError();
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
            return Util.serverError();
        }
    }

}
