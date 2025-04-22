package com.sbit.chatify.service;

import com.sbit.chatify.model.Response;
import com.sbit.chatify.model.UserDto;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface UserService {
    ResponseEntity<Response> isEmailExist(String email);

    String registerUser(UserDto userDto, RedirectAttributes redirectAttributes);

    String login(UserDto userDto, RedirectAttributes redirectAttributes, HttpSession session);
}
