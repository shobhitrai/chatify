package com.sbit.chatify.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.PageConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.dao.UserDao;
import com.sbit.chatify.entity.User;
import com.sbit.chatify.entity.UserDetail;
import jakarta.servlet.http.HttpSession;
import com.sbit.chatify.model.Response;
import com.sbit.chatify.model.UserDto;
import com.sbit.chatify.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ObjectMapper mapper;


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
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Response.builder()
                    .status(StatusConstant.INTERNAL_SERVER_ERROR_CODE)
                    .message(MessageConstant.INTERNAL_SERVER_ERROR).build());
        }
    }

    @Override
    public String registerUser(UserDto userDto, RedirectAttributes redirectAttributes) {
        boolean status = validateSignUp(userDto, redirectAttributes);
        if (!status)
            return PageConstant.REDIRECT_SIGNUP;
        try {
            User user = mapper.convertValue(userDto, User.class);
            user.setCreatedAt(new Date());
            user = userDao.save(user);
            UserDetail userDetails = new UserDetail();
            userDetails.setUserId(user.getId().toString());
            userDetails.setName(userDto.getName());
            userDao.save(userDetails);
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.USER_REGISTERED_SUCCESSFULLY);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.INTERNAL_SERVER_ERROR);
        }
        return PageConstant.REDIRECT_SIGNUP;
    }

    @Override
    public String login(UserDto userDto, RedirectAttributes redirectAttributes, HttpSession session) {
        boolean status = validateLogin(userDto, redirectAttributes);
        if (!status)
            return PageConstant.REDIRECT_LOGIN;
        try {
            User user = userDao.findByEmail(userDto.getEmail());
            if (Objects.isNull(user) || !userDto.getPassword().equals(user.getPassword())) {
                redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.INVALID_CREDENTIALS);
                return PageConstant.REDIRECT_LOGIN;
            }
            return PageConstant.REDIRECT_WALL;

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.INTERNAL_SERVER_ERROR);
            return PageConstant.REDIRECT_LOGIN;
        }
    }

    private boolean validateLogin(UserDto userDto, RedirectAttributes redirectAttributes) {
        if (Objects.isNull(userDto.getEmail()) || userDto.getEmail().isBlank()) {
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.EMAIL_IS_REQUIRED);
            return false;
        } else if (Objects.isNull(userDto.getPassword()) || userDto.getPassword().isBlank()) {
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.PASSWORD_IS_REQUIRED);
            return false;
        }
        return true;
    }

    private boolean validateSignUp(UserDto userDto, RedirectAttributes redirectAttributes) {
        if (Objects.isNull(userDto.getName()) || userDto.getName().isBlank()) {
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.NAME_IS_REQUIRED);
            return false;
        } else if (Objects.isNull(userDto.getEmail()) || userDto.getEmail().isBlank()) {
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.EMAIL_IS_REQUIRED);
            return false;
        } else if (Objects.isNull(userDto.getPassword()) || userDto.getPassword().isBlank()) {
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.PASSWORD_IS_REQUIRED);
            return false;
        }
        return true;
    }
}
