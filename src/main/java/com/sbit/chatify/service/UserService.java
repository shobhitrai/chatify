package com.sbit.chatify.service;

import com.sbit.chatify.model.Response;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<Response> isEmailExist(String email);
}
