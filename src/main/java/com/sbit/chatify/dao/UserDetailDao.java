package com.sbit.chatify.dao;

import com.sbit.chatify.entity.UserDetail;

public interface UserDetailDao {

    UserDetail save(UserDetail userDetails);

    UserDetail findByUserId(String string);
}
