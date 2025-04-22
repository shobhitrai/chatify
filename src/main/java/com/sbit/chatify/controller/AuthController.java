package com.sbit.chatify.controller;

import com.sbit.chatify.constant.PageConstant;
import com.sbit.chatify.constant.UrlConstant;
import com.sbit.chatify.model.Response;
import com.sbit.chatify.model.UserDto;
import com.sbit.chatify.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

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

    @GetMapping(UrlConstant.CHECK_EMAIL_EXIST)
    @ResponseBody
    public ResponseEntity<Response> isEmailExist(@PathVariable String email) {
        log.info("check-email-exist called {}", email);
        return userService.isEmailExist(email);
    }

    @PostMapping(UrlConstant.SIGNUP)
    public String signup(@ModelAttribute UserDto userDto, RedirectAttributes redirectAttributes) {
        log.info("Signup called {}", userDto.getEmail());
        return userService.registerUser(userDto, redirectAttributes);
    }

    @PostMapping(UrlConstant.LOGIN)
    public String login(@ModelAttribute UserDto userDto, RedirectAttributes redirectAttributes,
                        HttpSession session) {
        log.info("Login called {}", userDto.getEmail());
        return userService.login(userDto, redirectAttributes, session);
    }


}
