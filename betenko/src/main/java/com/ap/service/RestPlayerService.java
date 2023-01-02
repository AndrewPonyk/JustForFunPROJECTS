package com.ap.service;

import com.ap.config.Configuration;
import com.ap.domain.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestPlayerService {

    @Autowired
    private ObjectMapper objectMapper;

    public Player retrievePlayer() throws UnirestException, JsonProcessingException {
        String urlAsString = getUrlAsString(Configuration.CONFIG_HISTORY_USER_URL);
        return objectMapper.readValue(urlAsString, Player.class);
    }

    public int updatePlayerAndPost(Player player) throws JsonProcessingException, UnirestException {
        return postDataToUrl(Configuration.CONFIG_HISTORY_USER_URL, player);
    }

    private int postDataToUrl(String url, Object data) throws JsonProcessingException, UnirestException {
        HttpResponse<String> response = Unirest.post(url)
                .header("content-type", "application/json")
                .header("cache-control", "no-cache")
                .header("postman-token", "926b6156-45e1-281a-33fb-d6a468202f5b")
                .body(objectMapper.writeValueAsString(data))
                .asString();
        return response.getStatus();
    }

    public String getUrlAsString(String url) throws UnirestException {
        String getRequest = Unirest.get(url).asString().getBody();
        return getRequest;
    }

}
