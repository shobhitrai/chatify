package com.sbit.chatify.service.impl;

import com.sbit.chatify.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImpl implements UserService {

    @Autowired
    private HttpSession session;

    @Override
    public String getWallData() {
        return "";
    }
}
