package com.sbit.chatify.dao;

import com.sbit.chatify.entity.User;
import org.bson.types.ObjectId;

import java.util.List;

public interface UserDao {
    boolean isMailExist(String email);

    User save(User user);

    User findByEmail(String email);

    boolean isUsernameExist(String username);

    List<User> findByUserName(String username);

    User findById(ObjectId userId);
}
