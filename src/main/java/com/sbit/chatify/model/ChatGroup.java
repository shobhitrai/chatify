package com.sbit.chatify.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatGroup {
    private String senderId;
    private String receiverId;
    private String senderFirstName;
    private String senderLastName;
    private String senderProfileImage;
    private Boolean isRead;
    private Integer unreadCount;
    private List<ChatMessage> chats;
}
