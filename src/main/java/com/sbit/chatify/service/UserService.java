package com.sbit.chatify.service;

import com.sbit.chatify.model.Response;

public interface UserService {
    Response isEmailUnique(String email);
}
