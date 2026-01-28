package com.sbit.chatify.dao.impl;

import com.sbit.chatify.dao.ChatDao;
import com.sbit.chatify.entity.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChatDaoImpl implements ChatDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Chat chat) {
        mongoTemplate.save(chat);
    }

    @Override
    public List<Chat> getAllChatsByUserId(String userId) {
        var query = new Query();
        query.addCriteria(Criteria.where("receiverId").is(userId)
                .and("isActive").is(true).and("isRead").is(false));
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, Chat.class);
    }
}
