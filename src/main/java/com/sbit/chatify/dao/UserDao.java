package com.sbit.chatify.dao;

import com.sbit.chatify.entity.User;
import com.sbit.chatify.entity.UserDetail;

public interface UserDao {
    boolean isMailExist(String email);

    User save(User user);

    UserDetail save(UserDetail userDetails);

    User findByEmail(String email);
}
