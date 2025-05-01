package com.sbit.chatify.service;

import com.sbit.chatify.model.FriendRequestDto;
import com.sbit.chatify.model.UserDto;

public interface FriendReqService {

    void sendFriendRequest(String userId, FriendRequestDto friendRequestDto);

    void getSearchedUsers(String userId, UserDto userDto);
}
