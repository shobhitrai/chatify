package com.sbit.chatify.dao.impl;

import com.sbit.chatify.dao.ChatDao;
import com.sbit.chatify.entity.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
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
    public List<Chat> findLatestChatsForAllContact(String userId) {
        MatchOperation match = Aggregation.match(
                new Criteria().andOperator(Criteria.where("isActive").is(true),
                        new Criteria().orOperator(
                                Criteria.where("senderId").is(userId),
                                Criteria.where("receiverId").is(userId))));

//         Adds a new field contactId which is the other person in the chat, so we can group by contact.
        ProjectionOperation project = Aggregation.project()
                .andInclude("senderId", "receiverId", "message", "createdAt", "isActive")
                .and(
                        ConditionalOperators.when(Criteria.where("senderId").is(userId))
                                .thenValueOf("receiverId")
                                .otherwiseValueOf("senderId")
                ).as("contactId");

//        Sort chats newest first
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "createdAt");

//        Group chats by contactId
//        .first(Aggregation.ROOT) → take the first document per contact (i.e., the latest chat)
        GroupOperation group = Aggregation.group("contactId")
                .first(Aggregation.ROOT).as("chat");

//        The group stage nests the chat document inside "chat"
//        replaceRoot flattens it back to a normal Chat document
        ReplaceRootOperation replaceRoot = Aggregation.replaceRoot("chat");

//        The pipeline now: filter → compute contactId → sort → group by contact → flatten
        Aggregation aggregation = Aggregation.newAggregation(match, project, sort, group, replaceRoot);

        AggregationResults<Chat> results = mongoTemplate.aggregate(aggregation, "Chat", Chat.class);
        return results.getMappedResults();
    }


}
