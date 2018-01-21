package com.ap;

import com.ap.model.MomentResult;
import com.ap.sportsstatistics.Tennis;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Get45StageItems {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static final String POSSIBLE_COMEBACK = "SELECT * FROM BET_HISTORY\n" +
            "WHERE LAST_UPDATE > now() - INTERVAL 100 SECOND AND (NOTES like '%RE%') " +
            "order by ID desc";

    public static final String SELECT5_STRING = "SELECT * FROM BET_HISTORY\n" +
            "WHERE LAST_UPDATE > now() - INTERVAL 100 SECOND AND (STAGE LIKE '%:5%') " +
            "order by ID desc";

    public static final String SELECT12345_STRING = "SELECT * FROM BET_HISTORY\n" +
            "WHERE LAST_UPDATE > now() - INTERVAL 100 SECOND AND " +
            "(STAGE LIKE '%:4%' or STAGE LIKE '%:5%') " +
            "order by ID desc";

    private static final String ALL_CURRENT_ITEMS = "SELECT * FROM BET_HISTORY \n" +
            "WHERE LAST_UPDATE > now() - INTERVAL 100 SECOND " +
            "order by ID desc";;

    public static LinkedList<List<String>> get5Items() throws SQLException, IOException {
        return getBetItems(SELECT5_STRING);
    }

    public static LinkedList<List<String>> get12345Items() throws SQLException, IOException {
        return getBetItems(SELECT12345_STRING);
    }

    public static LinkedList<List<String>> getItemsWithPossibleComeback() throws IOException, SQLException {
        return getBetItems(POSSIBLE_COMEBACK);
    }

    public static LinkedList<List<String>> getAllCurrent() throws IOException, SQLException {
        return getBetItems(ALL_CURRENT_ITEMS);
    }

    private static LinkedList<List<String>> getBetItems(String selectString) throws SQLException, IOException {
        LinkedList<List<String>> result = new LinkedList<>();
        ResultSet resultSet = getResultSet(selectString);

        while (resultSet.next()) {
            String title = resultSet.getString(2);
            String stage = resultSet.getString("STAGE");
            String lastUpdate = resultSet.getString("LAST_UPDATE");
            String link = resultSet.getString("LINK");
            String results = resultSet.getString(4);
            String sport = resultSet.getString("SPORT");
            LinkedList<MomentResult> resultsList = objectMapper.readValue(results, new TypeReference<LinkedList<MomentResult>>() {
            });
            List<String> prevResults = getPlayerStagesFromHistory(title, sport);
            int favourite = stage.contains("player1")? 1 : 2;
            boolean coefDecrease = false;
            if(resultsList.size() > 1){
                MomentResult last = resultsList.getLast();
                MomentResult secondToLast = resultsList.get(resultsList.size()-2);

                coefDecrease = favourite == 1 ? (last.getCoef1() < secondToLast.getCoef1())
                        : (last.getCoef2() < secondToLast.getCoef2());
            }
            String resultColorProgress = coefDecrease ? "green" : "red";

            List<String> item = new ArrayList<>();
            item.add(title + " " + stage +" <b>" + sport +"</b>");
            item.add(String.format("<span style='color:%s'>"+resultsList.getLast().toString()+"</span>", resultColorProgress));
            if(sport.contains("Tennis")){
                item.add(Tennis.getPlayersStatsHasLeadershipInsetAndWin(title));
            }
            item.add(link);
            item.add(lastUpdate);
            item.addAll(prevResults);
            result.add(item);
        }

        return result;
    }

    public static List<String> getPlayerStagesFromHistory(String title, String sport) {
        List<String> result = new ArrayList<>();
        String[] parts = title.split(" - ");
        String sql = "SELECT * FROM BET_HISTORY WHERE (TITLE LIKE ? OR TITLE LIKE ?) AND SPORT = ? ";
        try {
            Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + parts[0] + "%");
            statement.setString(2, "%" + parts[1] + "%");
            statement.setString(3, sport);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                result.add(resultSet.getString("TITLE") + ":" + resultSet.getString("DATE")+":"
                        + resultSet.getString("STAGE"));
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return result;
    }


    /**
     * [date] stages 0,1,3,5 count=[n], stages 2,4 count=[m]
     * @return
     */
    public static String stagesCountCurrentDate() throws SQLException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String result =  df.format(new Date()) + ": ";
        Connection connection = ConnectionFactory.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM BET_HISTORY WHERE DATE =CURDATE() AND (STAGE LIKE '%0%' or STAGE LIKE '%:1%' OR STAGE LIKE '%:3%' OR STAGE LIKE '%:5%')");

        resultSet.next();
        result += "stages 0,1,3,5 count=[" + resultSet.getString(1) + "]";

        resultSet = statement.executeQuery("SELECT COUNT(*) FROM BET_HISTORY WHERE DATE =CURDATE() AND (STAGE LIKE '%2%' or STAGE LIKE '%:4%')");
        resultSet.next();
        result+= " stages 2,4 count=[" + resultSet.getString(1) + "]";
        return result;
    }

    private static ResultSet getResultSet(String selectString) throws SQLException {
        Connection connection = ConnectionFactory.getConnection();

        Integer counter = 0;
        Statement statement = connection.createStatement();
        return statement.executeQuery(selectString);
    }

    public static void main(String[] args) throws SQLException {
        System.out.println("test");
        System.out.println(stagesCountCurrentDate());
    }
}