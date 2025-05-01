package com.sbit.chatify.dao.impl;

import com.sbit.chatify.dao.ChatDao;
import com.sbit.chatify.entity.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ChatDaoImpl implements ChatDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Chat chat) {
        mongoTemplate.save(chat);
    }
}
