package com.sbit.chatify.controller;

import com.sbit.chatify.constant.PageConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class UserController {

    @GetMapping("/")
    public String root() {
        return PageConstant.REDIRECT_LOGIN;
    }

    @GetMapping("login")
    public String login() {
        return PageConstant.LOGIN;
    }

    @GetMapping("signup")
    public String signup() {
        return PageConstant.SIGN_UP;
    }

}
