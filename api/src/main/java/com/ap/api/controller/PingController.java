package com.ap.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/ping")
public class PingController {
    @RequestMapping("/")
    public String ping(){
        return "v4";
    }
}