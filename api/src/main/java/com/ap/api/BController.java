package com.ap.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController("/")
public class BController {
    private static Map<String, String> data = new HashMap<>();

    @RequestMapping
    public String test(){
        return "test";
    }
}