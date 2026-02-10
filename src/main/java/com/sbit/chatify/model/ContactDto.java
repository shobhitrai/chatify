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
