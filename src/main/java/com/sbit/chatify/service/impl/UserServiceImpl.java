package com.sbit.chatify.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.dao.FriendRequestDao;
import com.sbit.chatify.entity.FriendRequest;
import com.sbit.chatify.entity.User;
import com.sbit.chatify.model.FriendRequestDto;
import com.sbit.chatify.model.Response;
import com.sbit.chatify.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private FriendRequestDao friendRequestDao;

    @Override
    public ResponseEntity<Response> sendFriendRequest(FriendRequestDto friendRequestDto) {
        try {
            FriendRequest friendRequest = mapper.convertValue(friendRequestDto, FriendRequest.class);
            friendRequest.setCreatedAt(new Date());
            friendRequestDao.save(friendRequest);
            return ResponseEntity.ok(Response.builder()
                    .status(StatusConstant.SUCCESS_CODE)
                    .message(MessageConstant.SUCCESS).build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Response.builder()
                    .status(StatusConstant.INTERNAL_SERVER_ERROR_CODE)
                    .message(MessageConstant.INTERNAL_SERVER_ERROR).build());
        }
    }
}
