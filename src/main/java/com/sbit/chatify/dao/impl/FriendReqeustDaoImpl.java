package com.sbit.chatify.dao.impl;

import com.sbit.chatify.dao.FriendRequestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FriendReqeustDaoImpl implements FriendRequestDao {

    @Autowired
    private MongoTemplate mongoTemplate;
}
