package com.sbit.chatify.dao;

import com.sbit.chatify.entity.Chat;

import java.util.List;

public interface ChatDao {
    void save(Chat chat);

    List<Chat> findChatBySenderAndReceiverId(String senderId, String receiverId);

    List<Chat> findLatestChatsForAllContact(String userId);
}
