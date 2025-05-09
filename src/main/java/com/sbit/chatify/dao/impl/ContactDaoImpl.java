package com.sbit.chatify.dao.impl;

import com.sbit.chatify.dao.ContactDao;
import com.sbit.chatify.entity.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ContactDaoImpl implements ContactDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Contact save(Contact contact) {
        return mongoTemplate.save(contact);
    }

    @Override
    public Contact findByUserId(String userId) {
        var query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        return mongoTemplate.findOne(query, Contact.class);
    }
}
