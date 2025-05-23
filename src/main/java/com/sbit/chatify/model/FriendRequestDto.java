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
public class FriendRequestDto {
    private String receiverId;
    private String message;
    private String senderId;
    private Date createdAt;
    private Boolean isAccepted;
    private Boolean isActive;
    private String senderFirstName;
    private String senderLastName;
    private String formattedDate;
    private String senderProfileImage;
    private Boolean isRecent;
}
