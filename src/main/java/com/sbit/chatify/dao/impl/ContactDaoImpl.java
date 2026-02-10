package com.sbit.chatify.dao.impl;

import com.sbit.chatify.dao.ContactDao;
import com.sbit.chatify.entity.Contact;
import com.sbit.chatify.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ContactDaoImpl implements ContactDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Contact save(Contact contact) {
        return mongoTemplate.save(contact);
    }

    @Override
    public List<Contact> findByUserId(String userId) {
        var query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, Contact.class);
    }

    @Override
    public void seenLastMsg(String userId, String contactId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                .and("contacts").elemMatch(Criteria.where("contactId").is(contactId)));
        Update update = new Update();
        update.set("contacts.$.isLastMsgSeen", true);
        mongoTemplate.updateFirst(query, update, Contact.class);
    }

    @Override
    public boolean isFriend(String userId, String contactId) {
        var query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                .and("contactId").is(contactId));
        return mongoTemplate.exists(query, Contact.class);
    }

    @Override
    public void incrementUnseenMsg(String senderId, String contactId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(senderId)
                .and("contactId").is(contactId));

        Update update = new Update();
        update.set("lastUpdatedAt", Util.getCurrentDateTime());
        update.inc("unreadMsgCount", 1);

        mongoTemplate.updateFirst(query, update, Contact.class);
    }

    @Override
    public void resetUnseenMsg(String userId, String contactId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                .and("contactId").is(contactId));
        Update update = new Update();
        update.set("lastUpdatedAt", Util.getCurrentDateTime());
        update.set("unreadMsgCount", 0);
        mongoTemplate.updateFirst(query, update, Contact.class);
    }
}
