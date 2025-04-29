package com.sbit.chatify.dao;

import com.sbit.chatify.entity.FriendRequest;

public interface FriendRequestDao {
    FriendRequest save(FriendRequest friendRequest);

    boolean findBySenderIdAndReceiverId(String senderId, String receiverId);
}
