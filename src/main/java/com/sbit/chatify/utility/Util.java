package com.sbit.chatify.utility;

import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.model.Response;
import com.sbit.chatify.model.SocketResponse;
import org.springframework.http.ResponseEntity;

public class Util {

    public static ResponseEntity<Response> serverError() {
        return ResponseEntity.internalServerError().body(Response.builder()
                .status(StatusConstant.INTERNAL_SERVER_ERROR_CODE)
                .message(MessageConstant.INTERNAL_SERVER_ERROR).build());
    }

    public static SocketResponse serverError(String userId, String type) {
        return SocketResponse.builder().userId(userId)
                .status(StatusConstant.INTERNAL_SERVER_ERROR_CODE)
                .message(MessageConstant.INTERNAL_SERVER_ERROR)
                .type(type).build();
    }

    public static SocketResponse failure(String userId, String message, String type) {
        return SocketResponse.builder().userId(userId)
                .status(StatusConstant.FAILURE_CODE)
                .message(message)
                .type(type).build();
    }

    public static SocketResponse success(String userId, String type) {
        return SocketResponse.builder().userId(userId)
                .status(StatusConstant.SUCCESS_CODE)
                .message(MessageConstant.SUCCESS)
                .type(type).build();
    }


    public static ResponseEntity<Response> failure(String message) {
        return ResponseEntity.ok(Response.builder()
                .status(StatusConstant.FAILURE_CODE)
                .message(message).build());
    }

    public static ResponseEntity<Response> success() {
        return ResponseEntity.ok(Response.builder()
                .status(StatusConstant.SUCCESS_CODE)
                .message(MessageConstant.SUCCESS).build());
    }

    public static ResponseEntity<Response> success(Object data) {
        return ResponseEntity.ok(Response.builder()
                .status(StatusConstant.SUCCESS_CODE)
                .data(data)
                .message(MessageConstant.SUCCESS).build());
    }
}