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

public class _3FavoriteWinAfterLooseSet {
    public static final String SELECT_STRING = "SELECT * FROM BET_HISTORY\n" +
            "WHERE LAST_UPDATE > now() - INTERVAL 100 SECOND AND SPORT LIKE '%Ten%' order by ID desc";

//    public static final String SELECT_STRING = "SELECT * FROM BET_HISTORY\n" +
//            "WHERE DATE > '2017-10-28' AND SPORT LIKE '%Ten%' order by ID desc";

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    public static void main(String[] args) throws SQLException, IOException {
        processResultSet();
    }

    public static LinkedList<List<String>> processResultSet() throws SQLException, IOException {
        ResultSet result = getResultSet();
        LinkedList<List<String>> resultList = new LinkedList<>();
        int counter = 1;
        String date = "";
        int favouriteWin2nd = 0;
        int favouriteLose2nd = 0;
        int favouriteHasadvantageAndLose = 0;

        int playerFavourite = 0;
        while (result.next()) {
            List<String> matchDetails = new ArrayList<>();
            String title = result.getString(2);
            String stage = result.getString("STAGE");
            String lastUpdate = result.getString("LAST_UPDATE");
            String link = result.getString("LINK");

            if(title.contains("/")){
                continue;
            }

            playerFavourite = stage.contains("player1") ? 1 : 2;

            if (!date.equals(result.getString(5))) {
                //System.out.println("=================");
            }

            date = result.getString(5);
            matchDetails.add(title + " : " + stage + " : " + date + " : " + lastUpdate);
            matchDetails.add(link);
            String results = result.getString(4);
            LinkedList<MomentResult> resultsList = objectMapper.readValue(results, new TypeReference<LinkedList<MomentResult>>() {
            });

            int firstSetWinner = TennisScroreUtils.getWinner(resultsList.getLast().toString(), 1);
            int secondSetWinner = TennisScroreUtils.getWinner(resultsList.getLast().toString(), 2);

            if (/*secondSetWinner!= -1 &&*/ firstSetWinner != -1 && playerFavourite != firstSetWinner
             /*&& hasLeadershipInSet(resultsList, 2, playerFavourite)*/) {
                matchDetails.add(resultsList.getLast().toString());
                //System.out.println(counter + ")" + title + " " + date + " " + stage);
                if (secondSetWinner != -1 && firstSetWinner != secondSetWinner) {
                    System.out.println("favourite win 2nd" + title + date);
                    matchDetails.add("favourite win 2nd");
                    favouriteWin2nd++;
                } else {
                    System.out.println("favourite looooooooooooooooooooooooooooooooose 2nd" + title + date);
                    matchDetails.add("<b style='color:red'>favourite lose 2nd</b>");
                    favouriteLose2nd++;
                    if (TennisScroreUtils.hasLeadershipInSet(resultsList, 2, playerFavourite)) {
                        favouriteHasadvantageAndLose++;
                    } else {
                    }
                }

                //System.out.println(resultsList.getLast().toString());
                //System.out.println("Favourite Has leadership in 2nd:" + hasLeadershipInSet(resultsList, 2, playerFavourite));
                matchDetails.add(Tennis.getPlayersStatsHasLeadershipInsetAndWin(title));
                matchDetails.add("Favourite Has leadership in 2nd:" + TennisScroreUtils.hasLeadershipInSet(resultsList, 2, playerFavourite));

                counter++;
                resultList.add(matchDetails);
            }

        }

        System.out.println("\nFavourite win " + favouriteWin2nd);
        System.out.println("Favourite lose " + favouriteLose2nd);
        System.out.println("Favourite has advantage and lose " + favouriteHasadvantageAndLose);
        return resultList;
    }

    private static ResultSet getResultSet() throws SQLException {
        System.out.println("Test tennis after ");
        Connection connection = ConnectionFactory.getConnection();

        Statement statement = connection.createStatement();
        return statement.executeQuery(SELECT_STRING);
    }

}
