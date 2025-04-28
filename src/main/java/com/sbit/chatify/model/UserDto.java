package com.sbit.chatify.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String profileImage;
}
