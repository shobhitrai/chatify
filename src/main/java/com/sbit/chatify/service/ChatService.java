package com.sbit.chatify.service;

import com.sbit.chatify.model.ChatDto;
import com.sbit.chatify.model.ContactDto;

public interface ChatService {
    void seenLastMsg(String userId, ContactDto chatDto);
}
