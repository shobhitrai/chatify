package com.sbit.chatify.controller;

import com.sbit.chatify.constant.PageConstant;
import com.sbit.chatify.constant.UrlConstant;
import com.sbit.chatify.model.Response;
import com.sbit.chatify.model.UserDto;
import com.sbit.chatify.service.AuthenticateService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
public class AuthenticateController {

    @Autowired
    private AuthenticateService authenticateService;

    @GetMapping("/")
    public String root() {
        return PageConstant.REDIRECT_LOGIN;
    }

    @GetMapping(UrlConstant.LOGIN)
    public String login() {
        return PageConstant.LOGIN;
    }

    @GetMapping(UrlConstant.SIGNUP)
    public String signup() {
        return PageConstant.SIGN_UP;
    }

    @GetMapping(UrlConstant.LOGOUT)
    public String logout(HttpSession session) {
        log.info("logout called");
        return authenticateService.logout(session);
    }

    @PostMapping(UrlConstant.VALIDATE_SIGNUP)
    @ResponseBody
    public ResponseEntity<Response> validateSignUp(@RequestBody UserDto userDto) {
        log.info("validateSignUp called {}", userDto);
        return authenticateService.validateSignUp(userDto);
    }

    @PostMapping(UrlConstant.SIGNUP)
    public String signup(@ModelAttribute UserDto userDto,
                         RedirectAttributes redirectAttributes, HttpSession session) {
        log.info("signup called {}", userDto.getEmail());
        return authenticateService.registerUser(userDto, redirectAttributes, session);
    }

    @PostMapping(UrlConstant.LOGIN)
    public String login(@ModelAttribute UserDto userDto,
                        RedirectAttributes redirectAttributes, HttpSession session) {
        log.info("login called {}", userDto.getEmail());
        return authenticateService.login(userDto, redirectAttributes, session);
    }


}
