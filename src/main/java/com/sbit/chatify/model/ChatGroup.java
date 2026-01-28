package com.sbit.chatify.model;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChatGroup {
    private String senderId;
    private String receiverId;
    private String senderFirstName;
    private String senderLastName;
    private String senderProfileImage;
    private Boolean isRead;
    private Integer unreadCount;
    private List<ChatMessage> chats;
    private Boolean isSenderOnline;
}
