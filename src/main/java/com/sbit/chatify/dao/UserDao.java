package com.sbit.chatify.dao;

import com.sbit.chatify.entity.User;
import com.sbit.chatify.entity.UserDetail;

import java.util.List;

public interface UserDao {
    boolean isMailExist(String email);

    User save(User user);

    User findByEmail(String email);

    boolean isUsernameExist(String username);

    List<User> findByUserName(String username);
}
