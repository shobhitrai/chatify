package com.sbit.chatify.service.impl;

import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.SocketConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.dao.NotificationDao;
import com.sbit.chatify.dao.UserDetailDao;
import com.sbit.chatify.entity.Notification;
import com.sbit.chatify.entity.UserDetail;
import com.sbit.chatify.model.ChatGroup;
import com.sbit.chatify.model.NotificationDto;
import com.sbit.chatify.model.SocketResponse;
import com.sbit.chatify.service.NotificationService;
import com.sbit.chatify.utility.Util;
import com.sbit.chatify.websocket.SocketUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private UserDetailDao userDetailDao;

    @Autowired
    private NotificationDao notificationDao;

    @Override
    public void sendNotification(UserDetail senderDetail, String receiverId, String message) {

        var notification = Notification.builder().senderId(senderDetail.getUserId())
                .receiverId(receiverId).message(message).createdAt(new Date()).build();
        notificationDao.save(notification);

        if (SocketUtil.isUserConnected(receiverId)) {
            NotificationDto notificationDto = NotificationDto.builder().createdAt(notification.getCreatedAt())
                    .message(notification.getMessage()).senderId(notification.getSenderId())
                    .receiverId(receiverId).isRecent(Util.isRecent(notification.getCreatedAt()))
                    .formattedDate(Util.getNotificationFormatedDate(notification.getCreatedAt()))
                    .senderProfileImage(senderDetail.getProfileImage())
                    .senderFirstName(senderDetail.getFirstName())
                    .senderLastName(senderDetail.getLastName()).build();

            var socketResponse = SocketResponse.builder().userId(receiverId)
                    .status(StatusConstant.SUCCESS_CODE).message(MessageConstant.SUCCESS)
                    .type(SocketConstant.NOTIFICATION).data(notificationDto).build();
            SocketUtil.send(socketResponse);
        }
    }
}
