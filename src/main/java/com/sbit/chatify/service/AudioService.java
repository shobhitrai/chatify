package com.sbit.chatify.service;

import com.sbit.chatify.model.ContactDto;

public interface AudioService {
    void callRequest(String userId, ContactDto contactDto);
}
