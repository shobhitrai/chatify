package com.sbit.chatify.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.PageConstant;
import com.sbit.chatify.constant.ServicesConstant;
import com.sbit.chatify.dao.UserDao;
import com.sbit.chatify.dao.UserDetailDao;
import com.sbit.chatify.entity.User;
import com.sbit.chatify.entity.UserDetail;
import com.sbit.chatify.model.Response;
import com.sbit.chatify.model.UserDto;
import com.sbit.chatify.service.AuthenticateService;
import com.sbit.chatify.utility.Util;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class AuthenticateServiceImpl implements AuthenticateService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserDetailDao userDetailDao;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private HttpSession session;


    @Override
    public ResponseEntity<Response> validateSignUp(UserDto userDto) {
        try {
            if (userDto.getEmail() == null || userDto.getEmail().isBlank())
                return Util.failure(MessageConstant.EMAIL_IS_REQUIRED);

            if (userDto.getUsername() == null || userDto.getUsername().isBlank())
                return Util.failure(MessageConstant.USERNAME_IS_REQUIRED);

            boolean isEmailExist = userDao.isMailExist(userDto.getEmail());
            if (isEmailExist)
                return Util.failure(MessageConstant.EMAIL_ALREADY_EXISTS);

            boolean isUsernameExist = userDao.isUsernameExist(userDto.getUsername());
            if (isUsernameExist)
                return Util.failure(MessageConstant.USERNAME_ALREADY_TAKEN);

            return Util.success();

        } catch (Exception e) {
            e.printStackTrace();
            return Util.serverError();
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
            user.setIsActive(1);
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

            var userDetails = userDetailDao.findByUserId(user.getId().toString());
            setSessionAttributes(user, userDetails);
            return PageConstant.REDIRECT_WALL;

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(MessageConstant.ERROR, MessageConstant.INTERNAL_SERVER_ERROR);
            return PageConstant.REDIRECT_LOGIN;
        }
    }

    private void setSessionAttributes(User user, UserDetail userDetails) {
        session.setAttribute(MessageConstant.USER_ID, user.getId());
        session.setAttribute(MessageConstant.USER_NAME, user.getEmail());
        session.setAttribute(MessageConstant.FIRST_NAME, userDetails.getFirstName());
        session.setAttribute(MessageConstant.LAST_NAME, userDetails.getLastName());
        session.setAttribute(MessageConstant.PROFILE_IMAGE, userDetails.getProfileImage());
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
