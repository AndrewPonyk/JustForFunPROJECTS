package com.ap.service;

import com.ap.domain.BetItem;
import com.ap.domain.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;

@Service
public class BetPlacingService {
    @Autowired
    private RestPlayerService restPlayerService;

    @Autowired
    private ExecutorService executorService;

    public int placeBet(BetItem betItem) throws JsonProcessingException, UnirestException {
        Player player = restPlayerService.retrievePlayer();
        if(player.getBetItems() == null || player.getBetItems().isEmpty()){
            player.setBetItems(new LinkedList<>());
        }
        player.getBetItems().add(betItem);

        player.setSum(player.getSum()-betItem.getSum());

        executorService.submit(new BetStatusChecker(betItem));
        return restPlayerService.updatePlayerAndPost(player);
    }

}
