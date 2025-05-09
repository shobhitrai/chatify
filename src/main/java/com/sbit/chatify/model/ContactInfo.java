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
public class ContactInfo {
    private String contactId;
    private String contactName;
    private Date createdAt;
    private String profileImage;
}
