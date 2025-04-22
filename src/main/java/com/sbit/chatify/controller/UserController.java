package com.sbit.chatify.controller;

import com.sbit.chatify.constant.PageConstant;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class UserController {

    @Id
    private ObjectId id;

}
