package com.sbit.chatify.dao;

import com.sbit.chatify.entity.User;
import com.sbit.chatify.entity.UserDetails;

public interface UserDao {
    boolean isMailExist(String email);

    User save(User user);

    UserDetails save(UserDetails userDetails);

    User findByEmail(String email);
}
