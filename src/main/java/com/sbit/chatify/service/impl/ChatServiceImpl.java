package com.sbit.chatify.service.impl;

import com.sbit.chatify.dao.ContactDao;
import com.sbit.chatify.model.ChatDto;
import com.sbit.chatify.model.ContactDto;
import com.sbit.chatify.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ContactDao contactDao;

    @Override
    public void seenLastMsg(String userId, ContactDto contactDto) {
        contactDao.seenLastMsg(userId, contactDto.getContactId());
    }
}
