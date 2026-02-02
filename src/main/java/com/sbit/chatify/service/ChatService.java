package com.sbit.chatify.service;

import com.sbit.chatify.model.ContactDto;

public interface ChatService {
    void seenLastMsg(String userId, ContactDto chatDto);

    void getChat(String userId, ContactDto contactDto);
}
