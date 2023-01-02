package com.ap.service;

import com.ap.config.Configuration;
import com.ap.domain.BetItem;
import com.ap.domain.Player;
import com.ap.domain.SyncItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class BetStatusChecker implements Runnable {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private ThreadLocal<LocalDateTime> lastUpdate = new ThreadLocal<>();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    //for now it can be localdatetime
    private BetItem betItem;

    public BetStatusChecker(BetItem betItem) {
        this.betItem = betItem;
    }

    @Override
    public void run() {
        String lastResult = "";
        lastUpdate.set(LocalDateTime.now());

        while (true) {
            System.out.println(Thread.currentThread().toString() + "Trying to check bet with id" + betItem.getTitle() + "-" + betItem.getCreated());
            long between = Math.abs(ChronoUnit.SECONDS.between(lastUpdate.get(), LocalDateTime.now()));
            if (between > 30) {
                System.out.println("Last result  of |" + betItem.getTitle() + "| is:" + lastResult);
                break;
            }

            List<SyncItem> syncItemsApi = getSyncItemsApi(Configuration.FOOTBALL_ONLINE_ENDPOINTV2);
            for (SyncItem item : syncItemsApi) {
                if (item.title.toLowerCase().contains(betItem.getTitle().toLowerCase())
                        && Character.isDigit(item.title.charAt(item.title.length() - 1))
                        && Character.isDigit(item.title.charAt(item.title.length() - 2))) {

                    lastUpdate.set(LocalDateTime.now());
                    lastResult = item.title.substring(item.title.length() - 2);
                    System.out.println(Thread.currentThread().toString() + "CURRENT result is:" + lastResult);
                }
            }
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        checkAndUpdatePlayer(lastResult, betItem);
    }

    private static synchronized void checkAndUpdatePlayer(String lastResult, BetItem betItem) {
        String winner = getWinnerFromResult(lastResult);

        Player player = getPlayer(Configuration.CONFIG_HISTORY_USER_URL);
        //check if win
        if (winner.equals(betItem.getChose().toLowerCase()) ||
                (betItem.getChose().toLowerCase().length() > 1 && betItem.getChose().toLowerCase().contains(winner))) {
            //mark update
            player.setSum(player.getSum() + betItem.getSum() * betItem.getOdd());
            Optional<BetItem> first = player.getBetItems().stream()
                    .filter(e -> e.getCreated().equals(betItem.getCreated()) && e.getTitle().equalsIgnoreCase(betItem.getTitle()))
                    .findFirst();
            if (first.isPresent()) {
                first.get().setFinalResult(lastResult);
                first.get().setStatus(true);
            }
        } else {
            Optional<BetItem> first = player.getBetItems()
                    .stream()
                    .filter(e -> e.getCreated().equals(betItem.getCreated()) && e.getTitle().equalsIgnoreCase(betItem.getTitle()))
                    .findFirst();
            if (first.isPresent()) {
                first.get().setFinalResult(lastResult);
                first.get().setStatus(false);
            }
        }

        postDataToUrl(Configuration.CONFIG_HISTORY_USER_URL, player);
    }

    private static synchronized List<SyncItem> getSyncItemsApi(String url) {
        List<SyncItem> result = new LinkedList<>();

        try {
            HttpResponse<String> getRequest = Unirest.get(url).asString();
            //save last response
            LinkedHashMap<String, String> footballLive = objectMapper.readValue(getRequest.getBody(), new TypeReference<LinkedHashMap<String, String>>() {
            });
            if (footballLive.containsKey("value")) {
                String value = footballLive.get("value");
                String[] items = value.split(",");
                for (String item : items) {
                    String[] parts = item.split(";;");
                    if (parts.length == 5) {
                        SyncItem soccer = new SyncItem(parts[0], "", Double.valueOf(parts[2]), Double.valueOf(parts[3]), "soccer", parts[1], parts[4]);
                        result.add(soccer);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static synchronized Player getPlayer(String url) {
        try {
            HttpResponse<String> getRequest = Unirest.get(url).asString();
            return objectMapper.readValue(getRequest.getBody(), Player.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static synchronized String getWinnerFromResult(String result) {
        if (result == null || result.length() != 2) {
            return "NA";
        }

        Integer first = Integer.valueOf(result.charAt(0));
        Integer second = Integer.valueOf(result.charAt(1));

        if (first > second) {
            return "1";
        }
        if (second > first) {
            return "2";
        }
        return "x";
    }

    private static int postDataToUrl(String url, Object data) {
        try {
            HttpResponse<String> response = Unirest.post(url)
                    .header("content-type", "application/json")
                    .header("cache-control", "no-cache")
                    .header("postman-token", "926b6156-45e1-281a-33fb-d6a468202f5b")
                    .body(objectMapper.writeValueAsString(data))
                    .asString();
            return response.getStatus();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void main(String[] args) throws JsonProcessingException, UnirestException {
//        System.out.println(getSyncItemsApi(Configuration.FOOTBALL_LIVE));
        //System.out.println(getPlayer(Configuration.CONFIG_HISTORY_USER_URL));
    }
}
