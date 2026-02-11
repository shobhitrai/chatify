package com.sbit.chatify.dao;

import com.sbit.chatify.entity.Contact;

import java.util.List;

public interface ContactDao {
    Contact save(Contact contact);

    List<Contact> findByUserId(String userId);

    void seenLastMsg(String userId, String contactId);

    boolean isFriend(String userId, String contactId);

    void incrementUnseenMsg(String userId, String contactId);

    void resetUnseenMsg(String userId, String contactId);
}
