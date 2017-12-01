package com.ap;

import com.ap.ConnectionFactory;
import com.ap.model.MomentResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class _1CoefAnalysisLessThenSomeNumber {
    private static final double BET_LIMIT = 1.3;
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static void main(String[] args) throws SQLException, IOException {
        System.out.println("Hello World!");
        StringBuilder resultIllustrated = new StringBuilder("");
        Connection connection = ConnectionFactory.getConnection();
        Statement statement = connection.createStatement();
        int counter = 0;
        int less1_17AtStartCounter = 0;
        int winCount = 0;
        String ids = "";

        ResultSet result = statement.executeQuery("select TITLE,SPORT,STAGE,BET_TIME, RESULTS,ID from BET_HISTORY where DATE >= '2017-09-23' and " +
                "  DATE < '2017-10-29' AND ( STAGE like '%:5Com%') AND SPORT in ('Tennis','Basketball','Volleyball','Handball','Hockey','Badminton') " +
                "order by BET_TIME asc");

        while (result.next()) {
            counter++;
            String stage = result.getString(3);

            LinkedList<MomentResult> results = objectMapper.readValue(result.getString(5), new TypeReference<LinkedList<MomentResult>>() {
            });
            String title = result.getString(1);
            if (title.toLowerCase().contains("u-1") || title.toLowerCase().contains("u-2")) {
                continue;
            }
            if(results.getFirst().getResult().matches(".*[123456789].*")){
                continue;
            }
            if (stage.contains("player1")) {
                if (results.getFirst().getCoef1() < BET_LIMIT) {
                    System.out.println(results.getFirst().getCoef1());
                    System.out.println(results.getFirst().toString());
                    less1_17AtStartCounter++;
                    ids = ids + result.getString(6) + ",";
                    System.out.println("here");
                    System.out.println(title);
                    if (stage.contains("win")) {
                        winCount++;
                        System.out.println("WIN++");
                        resultIllustrated.append("1");
                    } else {
                        resultIllustrated.append("0");
                        System.out.println("LOSE++");
                    }
                }
            } else {
                if (results.getFirst().getCoef2() < BET_LIMIT) {
                    System.out.println(results.getFirst().getCoef2());
                    System.out.println(results.getFirst().toString());
                    less1_17AtStartCounter++;
                    ids = ids + result.getString(6) + ",";
                    System.out.println("here");
                    System.out.println(title);
                    if (stage.contains("win")) {
                        winCount++;
                        System.out.println("WIN++");
                        resultIllustrated.append("1");
                    } else {
                        resultIllustrated.append("0");
                        System.out.println("LOSE++");
                    }
                }
            }
            System.out.println("---------------------");
        }

        System.out.println(BET_LIMIT + " : LEss then count=" + less1_17AtStartCounter + "( of " + counter + " bets)");
        System.out.println("Win count = " + winCount);
        System.out.println("RATIO:" + (double)winCount/less1_17AtStartCounter);
        System.out.println(ids);
        System.out.println(resultIllustrated);
        statement.close();
        connection.close();
    }
}