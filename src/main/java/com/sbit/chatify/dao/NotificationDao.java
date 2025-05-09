package com.sbit.chatify.dao;

import com.sbit.chatify.entity.Notification;

import java.util.List;

public interface NotificationDao {
    void save(Notification notification);

    List<Notification> findByReceiverId(String userId);
}
