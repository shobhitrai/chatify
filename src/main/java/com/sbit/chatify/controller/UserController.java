package com.sbit.chatify.controller;

import com.sbit.chatify.constant.PageConstant;
import com.sbit.chatify.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("wall")
    public String wall() {
        return userService.getWallData();
    }
}
