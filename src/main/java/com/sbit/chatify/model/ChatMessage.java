package com.sbit.chatify.model;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChatMessage {
    private String message;
    private String type;
    private Date createdAt;
    private String formattedDate;
}
