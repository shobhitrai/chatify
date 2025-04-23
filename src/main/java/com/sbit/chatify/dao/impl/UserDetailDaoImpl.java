package com.sbit.chatify.dao.impl;

import com.sbit.chatify.dao.UserDetailDao;
import com.sbit.chatify.entity.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDetailDaoImpl implements UserDetailDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public UserDetail save(UserDetail userDetails) {
        return mongoTemplate.save(userDetails);
    }
}
