package com.ap.controller;

import com.ap.domain.Player;
import com.ap.service.RestPlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("player/")
public class PlayerController {

    @Autowired
    private RestPlayerService restPlayerService;

    @GetMapping("sortedHistory")
    public ResponseEntity<Player> getPlayer() throws JsonProcessingException, UnirestException {
        Player player = restPlayerService.retrievePlayer();
        player.getBetItems().sort((o1,o2)-> -1* o1.getCreated().compareTo(o2.getCreated()));

        return new ResponseEntity<>(player, HttpStatus.OK);
    }
}
