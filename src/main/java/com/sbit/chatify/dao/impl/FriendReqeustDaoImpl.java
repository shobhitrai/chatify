package com.sbit.chatify.dao.impl;

import com.sbit.chatify.dao.FriendRequestDao;
import com.sbit.chatify.entity.FriendRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FriendReqeustDaoImpl implements FriendRequestDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public FriendRequest save(FriendRequest friendRequest) {
        return mongoTemplate.save(friendRequest);
    }

    @Override
    public boolean isFriendRequestExist(String senderId, String receiverId) {
        var query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("senderId").is(senderId).and("receiverId").is(receiverId),
                Criteria.where("senderId").is(receiverId).and("receiverId").is(senderId)));
        query.addCriteria(Criteria.where("isAccepted").is(false).and("isActive").is(true));
        return mongoTemplate.exists(query, FriendRequest.class);
    }

    @Override
    public FriendRequest findBySenderIdAndReceiverId(String senderId, String receiverId) {
        var query = new Query();
        query.addCriteria(Criteria.where("senderId").is(senderId).and("receiverId").is(receiverId)
                .and("isAccepted").is(false).and("isActive").is(true));
        return mongoTemplate.findOne(query, FriendRequest.class);
    }

    @Override
    public List<FriendRequest> findActivePendingRequest(String userId) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("senderId").is(userId),
                Criteria.where("receiverId").is(userId)));
        query.addCriteria(Criteria.where("isActive").is(true)
                .and("isAccepted").is(false)
                .and("isCanceled").is(false));
        return mongoTemplate.find(query, FriendRequest.class);
    }
}
