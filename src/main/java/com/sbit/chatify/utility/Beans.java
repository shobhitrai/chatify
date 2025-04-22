package com.sbit.chatify.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

public class Beans {
    @Bean
    public ObjectMapper modelMapper() {
        return new ObjectMapper();
    }
}
