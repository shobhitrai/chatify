package com.sbit.chatify.dao.impl;

import com.sbit.chatify.dao.UserDao;
import com.sbit.chatify.entity.User;
import com.sbit.chatify.entity.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public boolean isMailExist(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        return mongoTemplate.exists(query, User.class);
    }

    @Override
    public User save(User user) {
        return mongoTemplate.save(user);
    }



    @Override
    public User findByEmail(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        return mongoTemplate.findOne(query, User.class);
    }

    @Override
    public boolean isUsernameExist(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        return mongoTemplate.exists(query, User.class);
    }

    @Override
    public List<User> findByUserName(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").regex(username, "i"));
        return mongoTemplate.find(query, User.class);
    }
}
