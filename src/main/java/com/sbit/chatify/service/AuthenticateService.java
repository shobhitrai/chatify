package com.sbit.chatify.service;

import com.sbit.chatify.model.Response;
import com.sbit.chatify.model.UserDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface AuthenticateService {
    ResponseEntity<Response> validateSignUp(UserDto userDto);

    String registerUser(UserDto userDto, RedirectAttributes redirectAttributes, HttpSession session);

    String login(UserDto userDto, RedirectAttributes redirectAttributes, HttpSession session);
}
