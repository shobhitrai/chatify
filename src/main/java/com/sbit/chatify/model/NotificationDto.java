package com.sbit.chatify.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {
    private String senderId;
    private String receiverId;
    private String message;
    private Date createdAt;
    private Boolean isRead;
    private String formattedDate;
    private String senderProfileImage;
    private Boolean isRecent;
}
