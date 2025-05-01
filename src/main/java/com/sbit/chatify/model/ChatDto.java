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
    private String receiverId;
    private String receiverFirstName;
    private String receiverLastName;
    private String message;
    private String type;
    private Date createdAt;
    private String formattedDate;
    private Boolean isRead;
}
