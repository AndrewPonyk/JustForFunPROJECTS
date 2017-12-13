package com.ap.sportsstatistics;


import com.ap.ConnectionFactory;
import com.ap.TennisScroreUtils;
import com.ap.model.MomentResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class Tennis {
    public static final String SELECT_STRING = "SELECT * FROM BET_HISTORY\n" +
            "WHERE DATE > '2017-10-25' AND SPORT LIKE '%%Tennis%%' AND TITLE like '%%%s%%' order by ID asc";
    private static ObjectMapper objectMapper = new ObjectMapper();


    public static String getPlayersStatsHasLeadershipInsetAndWin(String title)  {
        String[] players = title.split(" - ");
        if(players.length < 2 ){
            return "[length < 2 , something wrong, can not retrieve statistics]";
        }

        try {
            return playerHasLeadershipInsetAndWin(players[0]) + playerHasLeadershipInsetAndWin(players[1]);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "[something wrong, can not retrieve statistics]";
    }

    /**
     *
     * @return "<player>:win <n> of <m> sets, where has leadership"
     */
    public static String playerHasLeadershipInsetAndWin(String player) throws SQLException, IOException {
        int totalSets = 0;
        int totalSetsWhereHasLeadership = 0;
        int hasLeaderShipAndWin = 0;
        String statistics = "";
        ResultSet tennisResults = getTennisResults(player);
        while (tennisResults.next()){
            String title = tennisResults.getString("TITLE");
            int playerPosition = playerPosition(title, player);
            LinkedList<MomentResult> resultsList = objectMapper.readValue(tennisResults.getString("RESULTS"), new TypeReference<LinkedList<MomentResult>>() {
            });

            int set1Winner = TennisScroreUtils.getWinner(resultsList.getLast().getResult(), 1);
            int set2Winner = TennisScroreUtils.getWinner(resultsList.getLast().getResult(), 2);
            int set3Winner = TennisScroreUtils.getWinner(resultsList.getLast().getResult(), 3);

            if(set1Winner != -1){
                totalSets++;
                if(TennisScroreUtils.hasLeadershipInSet(resultsList,1,playerPosition)){
                    if(set1Winner == playerPosition){
                        hasLeaderShipAndWin++;
                        statistics +="1";
                    }else {
                        statistics+="0";
                    }
                    totalSetsWhereHasLeadership++;
                }
            }
            if(set2Winner != -1){
                totalSets++;
                if(TennisScroreUtils.hasLeadershipInSet(resultsList,2,playerPosition)){
                    if(set2Winner == playerPosition){
                        hasLeaderShipAndWin++;
                        statistics+="1";
                    }else {
                        statistics+="0";
                    }
                    totalSetsWhereHasLeadership++;
                }
            }
            if(set3Winner != -1){
                totalSets++;
                if(TennisScroreUtils.hasLeadershipInSet(resultsList,3,playerPosition)){
                    if(set3Winner == playerPosition){
                        hasLeaderShipAndWin++;
                        statistics+="1";
                    }else {
                        statistics+="0";
                    }
                    totalSetsWhereHasLeadership++;
                }
            }


            System.out.println(tennisResults.getString(1));
        }
        return player + ":win " + hasLeaderShipAndWin + " of " + totalSetsWhereHasLeadership + ", where has leadership "
                + "(" + statistics + ")" + "<br/>";
    }

    /**
     * Player got camback
     * @return ""
     */
    public static String playerHasLeaderShipBy2AndLoseSet(String player){
        return "";
    }

//-----------------------------------------------UTILS
    public static ResultSet getTennisResults(String player) throws SQLException {
        System.out.println("Test tennis after ");
        Connection connection = ConnectionFactory.getConnection();

        Statement statement = connection.createStatement();
        return statement.executeQuery(String.format(SELECT_STRING, player));
    }

    /**
     * 1 or 2
     * @return
     */
    public static int playerPosition(String title, String player){
        int i = title.indexOf(player);
        int delimiterPosition = title.indexOf(" - ");
        if(i<delimiterPosition){
            return 1;
        } else {
            return 2;
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
        System.out.println("Test");
        //String s = playerHasLeadershipInsetAndWin("Babos Timea");
        //System.out.println(s);
        System.out.println(getPlayersStatsHasLeadershipInsetAndWin("" +
                "Vallebuona Duilio - Wolf Jeffrey John"));
    }
}
