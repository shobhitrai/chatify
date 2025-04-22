package com.sbit.chatify.service.impl;

import com.sbit.chatify.model.Response;
import com.sbit.chatify.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {


    @Override
    public Response isEmailUnique(String email) {
        return null;
    }
}
