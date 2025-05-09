package com.sbit.chatify.dao;

import com.sbit.chatify.entity.FriendRequest;

import java.util.List;

public interface FriendRequestDao {
    FriendRequest save(FriendRequest friendRequest);

    boolean findBySenderIdAndReceiverId(String senderId, String receiverId);

    List<FriendRequest> findByReceiverId(String userId);
}
