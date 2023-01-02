package com.ap.controller;

import com.ap.domain.BetItem;
import com.ap.service.BetPlacingService;
import com.ap.service.RestPlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("placeBet")
public class BetPlacingController {
    @Autowired
    private BetPlacingService betPlacingService;

    @Autowired
    private RestPlayerService restPlayerService;

    @GetMapping("")
    public ResponseEntity<String> info(){
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }

    @PostMapping("")
    public HttpStatus placeBet(@RequestBody BetItem bet) throws JsonProcessingException, UnirestException {
        betPlacingService.placeBet(bet);

        return HttpStatus.OK;
    }

}
