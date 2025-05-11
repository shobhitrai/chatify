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
public class ContactDto {
    private String contactId;
    private String firstName;
    private String lastName;
    private Date createdAt;
    private String username;
    private String userId;
    private String profileImage;
    private Boolean isOnline;
}
