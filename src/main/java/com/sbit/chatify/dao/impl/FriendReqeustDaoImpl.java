package com.sbit.chatify.dao.impl;

import com.sbit.chatify.dao.FriendRequestDao;
import com.sbit.chatify.entity.FriendRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class FriendReqeustDaoImpl implements FriendRequestDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public FriendRequest save(FriendRequest friendRequest) {
        return mongoTemplate.save(friendRequest);
    }

    @Override
    public boolean findBySenderIdAndReceiverId(String senderId, String receiverId) {
        var query = new Query();
        query.addCriteria(Criteria.where("senderId").is(senderId)
                .and("receiverId").is(receiverId));
        return mongoTemplate.exists(query, FriendRequest.class);
    }
}
