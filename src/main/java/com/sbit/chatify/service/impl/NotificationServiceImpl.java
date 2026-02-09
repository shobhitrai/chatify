package com.sbit.chatify.service.impl;

import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.SocketConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.dao.ContactDao;
import com.sbit.chatify.dao.NotificationDao;
import com.sbit.chatify.dao.UserDetailDao;
import com.sbit.chatify.entity.Contact;
import com.sbit.chatify.entity.Notification;
import com.sbit.chatify.entity.UserDetail;
import com.sbit.chatify.model.NotificationDto;
import com.sbit.chatify.model.SocketResponse;
import com.sbit.chatify.service.NotificationService;
import com.sbit.chatify.utility.Util;
import com.sbit.chatify.websocket.SocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private UserDetailDao userDetailDao;

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private ContactDao contactDao;

    @Override
    public void sendNotification(UserDetail senderDetail, String receiverId, String message) {
        var notification = Notification.builder()
                .senderId(senderDetail.getUserId())
                .receiverId(receiverId)
                .message(message)
                .createdAt(new Date())
                .build();
        notificationDao.save(notification);

        if (SocketUtil.isUserConnected(receiverId)) {
            var notificationDto = NotificationDto.builder()
                    .createdAt(notification.getCreatedAt())
                    .message(notification.getMessage())
                    .senderId(notification.getSenderId())
                    .receiverId(receiverId)
                    .isRecent(Util.isRecent(notification.getCreatedAt()))
                    .formattedDate(Util.getNotificationFormatedDate(notification.getCreatedAt()))
                    .senderProfileImage(senderDetail.getProfileImage())
                    .senderFirstName(senderDetail.getFirstName())
                    .senderLastName(senderDetail.getLastName())
                    .build();

            var socketResponse = SocketResponse.builder()
                    .userId(receiverId)
                    .status(StatusConstant.SUCCESS_CODE)
                    .message(MessageConstant.SUCCESS)
                    .type(SocketConstant.NOTIFICATION)
                    .data(notificationDto)
                    .build();
            SocketUtil.send(socketResponse);
        }
    }

    @Override
    public void sendOfflineNotificationToContacts(String userId) {
        try {
            sendNotificationToContact(userId, SocketConstant.OFFLINE_NOTIFICATION);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error sending offline notification to contacts for userId: {}", userId);
        }
    }

    @Override
    public void sendOnlineNotificationToContacts(String userId) {
        try {
            sendNotificationToContact(userId, SocketConstant.ONLINE_NOTIFICATION);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error sending online notification to contacts for userId: {}", userId);
        }
    }

    private void sendNotificationToContact(String userId, String status) {
        List<Contact> contacts = contactDao.findByUserId(userId);
        contacts.stream()
                .filter(c -> SocketUtil.isUserConnected(c.getContactId())).forEach(c -> {
                    var socketResponse = SocketResponse.builder().userId(c.getContactId())
                            .status(StatusConstant.SUCCESS_CODE).message(MessageConstant.SUCCESS)
                            .type(status).data(userId).build();

                    SocketUtil.send(socketResponse);
                });
    }
}
