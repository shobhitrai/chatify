package com.sbit.chatify.controller;

import com.sbit.chatify.constant.UrlConstant;
import com.sbit.chatify.model.Response;
import com.sbit.chatify.model.UserDto;
import com.sbit.chatify.service.FriendReqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class FriendReqController {

    @Autowired
    private FriendReqService friendReqService;

//    @PostMapping(UrlConstant.SEND_FRIEND_REQUEST)
//    @ResponseBody
//    public ResponseEntity<Response> sendFriendRequest(@RequestBody FriendRequestDto friendRequestDto) {
//        log.info("sendFriendRequest called {}", friendRequestDto);
//        return friendReqService.sendFriendRequest(userId, friendRequestDto);
//    }

    @PostMapping(UrlConstant.SEARCH_USERS)
    @ResponseBody
    public ResponseEntity<Response> getSearchedUsers(@RequestBody UserDto userDto) {
        log.info("getSearchedUsers called {}", userDto);
        return friendReqService.getSearchedUsers(userDto);
    }

}
