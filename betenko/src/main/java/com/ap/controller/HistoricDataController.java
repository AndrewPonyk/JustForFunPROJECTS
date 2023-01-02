package com.ap.controller;

import com.ap.config.Configuration;
import com.ap.domain.SyncItem;
import com.ap.service.RestPlayerService;
import com.ap.utils.MapWithTimestamp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("historicdata/")
public class HistoricDataController {

    @Autowired
    private RestPlayerService restPlayerService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("")
    public ResponseEntity<MapWithTimestamp> getHistory(@RequestParam(required = false, defaultValue = "") String player1Title,
                                                       @RequestParam(required = false, defaultValue = "") String player2Title,
                                                       @RequestParam(required = false, defaultValue = "") String sport) throws JsonProcessingException, UnirestException {
        String urlAsString = restPlayerService.getUrlAsString(Configuration.ORACLE_URL);
        MapWithTimestamp mapWithTimestamp = MapWithTimestamp.parseJson(urlAsString);
        Map<Pair<String, LocalDateTime>, LinkedList<SyncItem>> collect = mapWithTimestamp.getData().entrySet().stream().filter(e -> {
            if (e.getValue().isEmpty()) {
                return false;
            }
            SyncItem lastScore = e.getValue().getLast();
            boolean finalCondition = true;
            if (!StringUtils.isEmpty(player1Title)) {
                finalCondition = finalCondition && lastScore.title.toLowerCase().contains(player1Title.toLowerCase());
            }
            if (!StringUtils.isEmpty(player2Title)) {
                finalCondition = finalCondition && lastScore.title.toLowerCase().contains(player2Title.toLowerCase());
            }
            finalCondition = finalCondition && lastScore.sport.toLowerCase().contains(sport.toLowerCase());
            return finalCondition;
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new ResponseEntity<>(new MapWithTimestamp(collect), HttpStatus.OK);
    }
}