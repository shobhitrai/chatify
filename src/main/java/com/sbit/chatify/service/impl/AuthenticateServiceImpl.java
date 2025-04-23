package com.sbit.chatify.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.PageConstant;
import com.sbit.chatify.constant.ServicesConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.dao.UserDao;
import com.sbit.chatify.dao.UserDetailDao;
import com.sbit.chatify.entity.User;
import com.sbit.chatify.entity.UserDetail;
import com.sbit.chatify.model.Response;
import com.sbit.chatify.model.UserDto;
import com.sbit.chatify.service.AuthenticateService;
import com.sbit.chatify.utility.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AuthenticateServiceImpl implements AuthenticateService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserDetailDao userDetailDao;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JwtService jwtService;


    @Override
    public ResponseEntity<Response> validateSignUp(UserDto userDto) {
        try {
            if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
                return ResponseEntity.ok(Response.builder()
                        .status(StatusConstant.ERROR_CODE)
                        .message(MessageConstant.EMAIL_IS_REQUIRED).build());
            }
            if (userDto.getUsername() == null || userDto.getUsername().isBlank()) {
                return ResponseEntity.ok(Response.builder()
                        .status(StatusConstant.ERROR_CODE)
                        .message(MessageConstant.USERNAME_IS_REQUIRED).build());
            }

            boolean isEmailExist = userDao.isMailExist(userDto.getEmail());
            if (isEmailExist)
                return ResponseEntity.ok(Response.builder()
                        .status(StatusConstant.ERROR_CODE)
                        .message(MessageConstant.EMAIL_ALREADY_EXISTS).build());

            boolean isUsernameExist = userDao.isUsernameExist(userDto.getUsername());
            if (isUsernameExist)
                return ResponseEntity.ok(Response.builder()
                        .status(StatusConstant.ERROR_CODE)
                        .message(MessageConstant.USER_ALREADY_EXISTS).build());

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
        var status = validateSignUp(userDto, redirectAttributes);
        if (!status)
            return PageConstant.REDIRECT_SIGNUP;
        try {
            var user = mapper.convertValue(userDto, User.class);
            user.setCreatedAt(new Date());
            user = userDao.save(user);
            var userDetails = new UserDetail();
            userDetails.setUserId(user.getId().toString());
            userDetails.setFirstName(userDto.getFirstName());
            userDetails.setLastName(userDto.getLastName());
            userDetails.setProfileImage(ServicesConstant.DEFAULT_PROFILE_IMAGE);

            userDetailDao.save(userDetails);
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.USER_REGISTERED_SUCCESSFULLY);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.INTERNAL_SERVER_ERROR);
        }
        return PageConstant.REDIRECT_SIGNUP;
    }

    @Override
    public String login(UserDto userDto, RedirectAttributes redirectAttributes) {
        var status = validateLogin(userDto, redirectAttributes);
        if (!status)
            return PageConstant.REDIRECT_LOGIN;
        try {
            var user = userDao.findByEmail(userDto.getEmail());
            if (Objects.isNull(user) || !userDto.getPassword().equals(user.getPassword())) {
                redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.WRONG_LOGIN_OR_PASSWORD);
                return PageConstant.REDIRECT_LOGIN;
            }

            String jwtToken = getJwtToken(user);
            redirectAttributes.addFlashAttribute(MessageConstant.TOKEN, jwtToken);
            redirectAttributes.addFlashAttribute(MessageConstant.TOKEN_TIME, jwtService.getExpirationTime());
            return PageConstant.REDIRECT_WALL;

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.INTERNAL_SERVER_ERROR);
            return PageConstant.REDIRECT_LOGIN;
        }
    }

    private String getJwtToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("userId", user.getId());
        claims.put("userName", user.getUsername());
        return jwtService.generateToken(claims, user);
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
        if (Objects.isNull(userDto.getFirstName()) || userDto.getFirstName().isBlank()) {
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.FIRST_NAME_IS_REQUIRED);
            return false;
        } else if (Objects.isNull(userDto.getLastName()) || userDto.getLastName().isBlank()) {
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.LAST_NAME_IS_REQUIRED);
            return false;
        } else if (Objects.isNull(userDto.getEmail()) || userDto.getEmail().isBlank()) {
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.EMAIL_IS_REQUIRED);
            return false;
        } else if (Objects.isNull(userDto.getUsername()) || userDto.getUsername().isBlank()) {
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.USERNAME_IS_REQUIRED);
            return false;
        } else if (Objects.isNull(userDto.getPassword()) || userDto.getPassword().isBlank()) {
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.PASSWORD_IS_REQUIRED);
            return false;
        }
        return true;
    }
}
