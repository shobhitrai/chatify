package com.sbit.chatify.service.impl;

import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.PageConstant;
import com.sbit.chatify.dao.UserDao;
import com.sbit.chatify.dao.UserDetailDao;
import com.sbit.chatify.entity.User;
import com.sbit.chatify.entity.UserDetail;
import com.sbit.chatify.service.UserService;
import com.sbit.chatify.websocket.SocketUtil;
import jakarta.servlet.http.HttpSession;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private HttpSession session;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserDetailDao userDetailDao;

    @Override
    public String getWallData(Model model) {
        String userId = (String) session.getAttribute("userId");
        if (Objects.isNull(userId) || SocketUtil.SOCKET_CONNECTION.containsKey(userId))
            return PageConstant.REDIRECT_LOGIN;

        User user = userDao.findById(new ObjectId(userId));
        UserDetail userDetail = userDetailDao.findByUserId(userId);
        model.addAttribute(MessageConstant.USER_ID, user.getId());
        model.addAttribute(MessageConstant.USER_NAME, user.getUsername());
        model.addAttribute(MessageConstant.FIRST_NAME, userDetail.getFirstName());
        model.addAttribute(MessageConstant.LAST_NAME, userDetail.getLastName());
        model.addAttribute(MessageConstant.PROFILE_IMAGE, userDetail.getProfileImage());
        return PageConstant.WALL;
    }
}
