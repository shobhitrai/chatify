package com.sbit.chatify.dao.impl;

import com.sbit.chatify.dao.ChatDao;
import com.sbit.chatify.entity.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    public List<Chat> findChatBySenderAndReceiverId(String senderId, String receiverId) {
        var query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("senderId").is(senderId).and("receiverId").is(receiverId),
                Criteria.where("senderId").is(receiverId).and("receiverId").is(senderId)));
        query.addCriteria(Criteria.where("isActive").is(true));
        query.with(Sort.by(Sort.Direction.ASC, "createdAt"));
        return mongoTemplate.find(query, Chat.class);
    }

    @Override
    public Chat findLatestChatBetweenUser(String userId, String contactId) {
        var query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("senderId").is(userId).and("receiverId").is(contactId),
                Criteria.where("senderId").is(contactId).and("receiverId").is(userId)));
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        query.limit(1);
        return mongoTemplate.findOne(query, Chat.class);
    }
}
