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
public class ChatDto {
    private String senderId;
    private String receiverId;
    private String senderFirstName;
    private String senderLastName;
    private String senderProfileImage;
    private String message;
    private String type;
    private Date createdAt;
    private String formattedDate;
    private Boolean isRead;
}
