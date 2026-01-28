package com.sbit.chatify.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

public interface WallService {
    String getWallData(Model model, HttpSession session);
}
