package com.sbit.chatify.controller;

import com.sbit.chatify.constant.UrlConstant;
import com.sbit.chatify.service.WallService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class WallController {

    @Autowired
    private WallService wallService;

    @GetMapping(UrlConstant.WALL)
    public String getWallData(Model model, HttpSession session) {
        return wallService.getWallData(model, session);
    }

}
