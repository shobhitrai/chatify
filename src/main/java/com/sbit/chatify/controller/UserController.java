package com.sbit.chatify.controller;

import com.sbit.chatify.constant.PageConstant;
import com.sbit.chatify.constant.UrlConstant;
import com.sbit.chatify.model.FriendRequestDto;
import com.sbit.chatify.model.Response;
import com.sbit.chatify.model.UserDto;
import com.sbit.chatify.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(UrlConstant.SEND_FRIEND_REQUEST)
    @ResponseBody
    public ResponseEntity<Response> sendFriendRequest(@RequestBody FriendRequestDto friendRequestDto) {
        log.info("sendFriendRequest called {}", friendRequestDto);
        return userService.sendFriendRequest(friendRequestDto);
    }

}
