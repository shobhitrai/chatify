package com.sbit.chatify.dao.impl;

import com.sbit.chatify.dao.NotificationDao;
import com.sbit.chatify.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationDaoImpl implements NotificationDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Notification notification) {
        mongoTemplate.save(notification);
    }

    @Override
    public List<Notification> findByReceiverId(String userId) {
        var query = new Query();
        query.addCriteria(Criteria.where("receiverId").is(userId));
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, Notification.class);
    }
}
