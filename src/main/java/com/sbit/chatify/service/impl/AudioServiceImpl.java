package com.sbit.chatify.service.impl;

import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.SocketConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.dao.UserDao;
import com.sbit.chatify.dao.UserDetailDao;
import com.sbit.chatify.model.ContactDto;
import com.sbit.chatify.model.SocketResponse;
import com.sbit.chatify.model.UserDto;
import com.sbit.chatify.service.AudioService;
import com.sbit.chatify.websocket.SocketUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AudioServiceImpl implements AudioService {

    @Autowired
    private UserDetailDao userDetailDao;

    @Override
    public void callRequest(String userId, ContactDto contactDto) {
        SocketResponse socketResponse = null;

        try {
            var userDetail = userDetailDao.findByUserId(userId);
            if (userDetail.getIsOnCall()) {
                socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.FAILURE_CODE)
                        .message(MessageConstant.YOU_ARE_ALREADY_ON_A_CALL).type(SocketConstant.ACK_CALL_REQUEST).build();
                return;
            }

            var contactDetail = userDetailDao.findByUserId(contactDto.getContactId());
            if (contactDetail.getIsOnCall()) {
                socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.FAILURE_CODE)
                        .message(contactDetail.getFirstName() + MessageConstant.IS_ON_ANOTHER_CALL)
                        .type(SocketConstant.ACK_CALL_REQUEST).build();
                return;
            }

            UserDto caller = UserDto.builder()
                    .userId(userDetail.getUserId())
                    .firstName(userDetail.getFirstName())
                    .lastName(userDetail.getLastName())
                    .profileImage(userDetail.getProfileImage())
                    .build();
            socketResponse = SocketResponse.builder().userId(contactDto.getContactId()).status(StatusConstant.SUCCESS_CODE)
                    .message(MessageConstant.SUCCESS).data(caller).type(SocketConstant.CALL_ACCEPT_DENY).build();

        } catch (Exception e) {
            socketResponse = SocketResponse.builder().userId(userId).status(StatusConstant.INTERNAL_SERVER_ERROR_CODE)
                    .message(MessageConstant.INTERNAL_SERVER_ERROR).type(SocketConstant.ACK_CALL_REQUEST).build();
        } finally {
            SocketUtil.send(socketResponse);
        }
    }
}
