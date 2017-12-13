package com.ap;

import com.ap.model.MomentResult;
import com.ap.sportsstatistics.Tennis;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Get45StageItems {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    public static final String SELECT_STRING = "SELECT * FROM BET_HISTORY\n" +
            "WHERE LAST_UPDATE > now() - INTERVAL 100 SECOND AND (STAGE LIKE '%:3%' OR STAGE LIKE '%:4%' OR STAGE LIKE '%:5%') " +
            "order by ID desc";


    public static List<List<String>> get45Items() throws SQLException, IOException {
        List<List<String>> result = new ArrayList<>();
        ResultSet resultSet = getResultSet();

        while (resultSet.next()) {

            String title = resultSet.getString(2);
            String stage = resultSet.getString("STAGE");
            String lastUpdate = resultSet.getString("LAST_UPDATE");
            String link = resultSet.getString("LINK");
            String results = resultSet.getString(4);
            String sport = resultSet.getString("SPORT");
            LinkedList<MomentResult> resultsList = objectMapper.readValue(results, new TypeReference<LinkedList<MomentResult>>() {
            });
            int favourite = stage.contains("player1")? 1 : 2;
            MomentResult last = resultsList.getLast();
            MomentResult secondToLast = resultsList.get(resultsList.size()-2);

            boolean coefDecrease = favourite == 1 ? (last.getCoef1() < secondToLast.getCoef1())
                    : (last.getCoef2() < secondToLast.getCoef2());
            String resultColorProgress = coefDecrease ? "green" : "red";

            List<String> item = new ArrayList<>();
            item.add(title + " " + stage +" <b>" + sport +"</b>");
            item.add(String.format("<span style='color:%s'>"+resultsList.getLast().toString()+"</span>", resultColorProgress));
            if(sport.contains("Tennis")){
                item.add(Tennis.getPlayersStatsHasLeadershipInsetAndWin(title));
            }
            item.add(link);
            item.add(lastUpdate);
            result.add(item);
        }

        return result;
    }

    private static ResultSet getResultSet() throws SQLException {
        Connection connection = ConnectionFactory.getConnection();

        Integer counter = 0;
        Statement statement = connection.createStatement();
        return statement.executeQuery(SELECT_STRING);
    }
}