package com.sbit.chatify.service.impl;

import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.dao.UserDao;
import com.sbit.chatify.model.Response;
import com.sbit.chatify.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;


    @Override
    public ResponseEntity<Response> isEmailExist(String email) {
        try {
            boolean isEmailExist = userDao.isMailExist(email);
            if (isEmailExist)
                return ResponseEntity.ok(Response.builder()
                        .status(StatusConstant.ERROR_CODE)
                        .message(MessageConstant.EMAIL_ALREADY_EXISTS).build());
            else
                return ResponseEntity.ok(Response.builder()
                        .status(StatusConstant.SUCCESS_CODE)
                        .message(MessageConstant.EMAIL_IS_AVAILABLE).build());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Response.builder()
                    .status(StatusConstant.INTERNAL_SERVER_ERROR_CODE)
                    .message(MessageConstant.INTERNAL_SERVER_ERROR).build());
        }
    }
}
