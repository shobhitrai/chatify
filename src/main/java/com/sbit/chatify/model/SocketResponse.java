package com.sbit.chatify.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocketResponse {
    private String userId;
    private int status;
    private String message;
    private String type;
    private Object data;

}
