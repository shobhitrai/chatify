package com.sbit.chatify.dao;

import com.sbit.chatify.entity.FriendRequest;

import java.util.List;

public interface FriendRequestDao {
    FriendRequest save(FriendRequest friendRequest);

    boolean isFriendRequestExist(String senderId, String receiverId);

    FriendRequest findBySenderIdAndReceiverId(String senderId, String receiverId);

    FriendRequest findActivePendingRequest(String userId, String contactId);

    List<FriendRequest> findActivePendingRequest(String userId);
}
