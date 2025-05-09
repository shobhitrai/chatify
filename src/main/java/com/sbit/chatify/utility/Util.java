package com.sbit.chatify.utility;

import com.sbit.chatify.constant.MessageConstant;
import com.sbit.chatify.constant.StatusConstant;
import com.sbit.chatify.model.Response;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    public static String getFormatedDate(Date date) {
        var formatter = new SimpleDateFormat("MMM dd, yyyy");
        return formatter.format(date);
    }

    public static boolean isRecent(Date givenDate) {
        long currentTime = System.currentTimeMillis();
        long oneWeekAgo = currentTime - TimeUnit.DAYS.toMillis(2);
        return givenDate.getTime() > oneWeekAgo;
    }
}