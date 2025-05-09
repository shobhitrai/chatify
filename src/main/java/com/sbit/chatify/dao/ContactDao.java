package com.sbit.chatify.dao;

import com.sbit.chatify.entity.Contact;

public interface ContactDao {
    Contact save(Contact contact);

    Contact findByUserId(String userId);

}
