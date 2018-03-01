package com.ap.api.controller;

import com.ap.api.entity.Status;
import com.ap.api.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController("/")
public class BController {
    private static Map<String, String> data = new HashMap<>();
    @Autowired
    StatusRepository repository;

    @RequestMapping
    public HttpStatus index(){
        return HttpStatus.OK;
    }

    @RequestMapping("/statuses")
    public String statuses(){
        StringBuilder result = new StringBuilder();
        Iterable<Status> statuses = repository.findAll();
        statuses.forEach(status -> {
            result.append("<a href='/status?d=").append(status.getKey()).append("'>")
                    .append(status.getKey()).append("</a><br/>");
        });
        return result.toString();
    }

    @RequestMapping("/status")
    public String test(@RequestParam(value = "d", required = false) String date){
        if (date==null){
            LocalDate now = LocalDate.now();
            date = now.toString();
        }

        Status status = repository.findOne(date);
        String indexLink = "<a href='/statuses'>index</a><br/>";
        return status != null ? indexLink+status.getValue() : indexLink+"empty";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/status")
    public HttpStatus addCurrentDateStatus(@RequestBody String status){
        LocalDate now = LocalDate.now();
        Status statusEntity = new Status();
        statusEntity.setKey(now.toString());
        statusEntity.setValue(status);

        Status existing = repository.findOne(now.toString());
        if(existing != null && existing.getValue().length()/2 > status.length()){
            return HttpStatus.BAD_REQUEST;
        }

        repository.save(statusEntity);
        return HttpStatus.OK;
    }
}