package com.sbit.chatify.service;

import com.sbit.chatify.model.FriendRequestDto;
import com.sbit.chatify.model.Response;
import com.sbit.chatify.model.UserDto;
import org.springframework.http.ResponseEntity;

public interface FriendReqService {

    void sendFriendRequest(String userId, FriendRequestDto friendRequestDto);

    ResponseEntity<Response> getSearchedUsers(UserDto userDto);
}
