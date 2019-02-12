package com.ap.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController("/ping")
public class PingController {
    @RequestMapping("/")
    public String ping(){
        return LocalDateTime.now().toString() + "v5";
    }

    public static void main(String[] args) {
        String test = "123$abc$hello";


        System.out.println(test.substring(test.indexOf("$")+1, test.lastIndexOf("$")));

    }
}