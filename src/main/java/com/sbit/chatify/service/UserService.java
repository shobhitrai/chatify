package com.sbit.chatify.service;

import com.sbit.chatify.model.FriendRequestDto;
import com.sbit.chatify.model.Response;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<Response> sendFriendRequest(FriendRequestDto friendRequestDto);
}
