package com.ap.utils;

import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ChronoUnitExample {
    public static void main(String[] args) {
        System.out.println("TEST");

        Pair<String,String> item = Pair.of("21481:>[[2021-06-13T14:51:31.913]]","");
        System.out.println("Check if ALREADY sent:" + "");
        if(item.getKey().contains("[[")){
            String dateTime = item.getKey().substring(item.getKey().indexOf("[[") + 2, item.getKey().indexOf("]]"));
            System.out.println(item.getKey());
            LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
            long diff = ChronoUnit.MINUTES.between(localDateTime, LocalDateTime.now());
            System.out.println(diff);
            if(Math.abs(diff) < 34){
                System.out.println("!!! ALREDY SENT");
                return;
            }
        }

    }
}
