package com.sbit.chatify.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatDto {
    private String senderId;
    private String receiverId;
    private String message;
    private String type;
    private Date createdAt;
    private String formattedDate;
    private Boolean isRead;
}
