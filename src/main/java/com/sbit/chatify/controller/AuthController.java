package com.sbit.chatify.controller;

import com.sbit.chatify.constant.PageConstant;
import com.sbit.chatify.constant.UrlConstant;
import com.sbit.chatify.model.Response;
import com.sbit.chatify.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping(UrlConstant.CHECK_EMAIL_UNIQUENESS)
    @ResponseBody
    public ResponseEntity<Response> isEmailUnique(@RequestParam String email) throws Exception {
        try {
            return ResponseEntity.ok().body(userService.isEmailUnique(email));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(Response.builder()
                    .status(105).message(e.getMessage()).build());
        }

    }


}
