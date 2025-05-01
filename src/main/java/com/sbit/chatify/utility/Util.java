package com.sbit.chatify.utility;

import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.model.Response;
import org.springframework.http.ResponseEntity;

public class Util {

    public static ResponseEntity<Response> serverError() {
        return ResponseEntity.internalServerError().body(Response.builder()
                .status(StatusConstant.INTERNAL_SERVER_ERROR_CODE)
                .message(MessageConstant.INTERNAL_SERVER_ERROR).build());
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