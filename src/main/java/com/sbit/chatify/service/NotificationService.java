package com.sbit.chatify.service;

import com.sbit.chatify.entity.UserDetail;

public interface NotificationService {

    void sendNotification(UserDetail senderDetail, String receiverId, String message);

    void sendOfflineNotificationToContacts(String userId);

    void sendOnlineNotificationToContacts(String userId);
}
