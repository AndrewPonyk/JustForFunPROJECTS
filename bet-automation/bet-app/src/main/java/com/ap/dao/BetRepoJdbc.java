package com.ap.dao;

import com.ap.model.BetItem;
import com.ap.model.JsonMapper;
import com.ap.model.MomentResult;
import com.ap.utils.Constants;
import com.ap.utils.SystemUtils;
import com.ap.wincheckers.WinChecker;
import com.ap.wincheckers.WinCheckerProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;

public class BetRepoJdbc implements BetRepo {

    Logger logger = LoggerFactory.getLogger(BetRepoJdbc.class);
    Random random = new Random();
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<BetItem> getAll() {
        try {
            Connection connection = ConnectionFactory.getConnection();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveUpdateItems(LinkedList<BetItem> betItems) {
        if(betItems.isEmpty()){
            System.err.println("EMPTY betItems!!!!!!");
            return;
        }
        Date sqlDateNow = new Date(Calendar.getInstance().getTime().getTime());
        Timestamp sqlDateTimeNow = new Timestamp(new java.util.Date().getTime());
        java.util.Date yesterday = new java.util.Date();
        Long within27H = yesterday.getTime() - (27*60*60*1000L);

        try {
            Connection connection = ConnectionFactory.getConnection();

            StringBuilder betCondition = new StringBuilder("(");
            for (BetItem item : betItems) {
                betCondition.append("'").append(item.getTitle()).append(item.getSport()).append("',");
            }
            betCondition.setLength(betCondition.length() - 1); // remove comma
            betCondition.append(")");

            HashMap<String, BetItem> existingBets = new HashMap<>();
            PreparedStatement ps = connection.prepareStatement("select * from BET_HISTORY where concat(TITLE,'', SPORT)  in " +
                    betCondition + " AND BET_TIME > (now() - INTERVAL 300 MINUTE )");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BetItem item = new BetItem(rs.getString("TITLE"), rs.getString("SPORT"),
                        JsonMapper.mapper.readValue(rs.getString("RESULTS"), new TypeReference<LinkedList<MomentResult>>() {
                        }), rs.getString("STAGE"), "", rs.getString("NOTES"), rs.getString("COMPETITION"));
                item.setDate(rs.getDate("DATE"));
                existingBets.put(item.getTitle() + item.getSport(), item);
            }
            PreparedStatement insertPs = connection.prepareStatement("INSERT INTO BET_HISTORY (TITLE, SPORT, RESULTS, DATE, STAGE, LINK, COMPETITION, LIVE_STREAM)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            PreparedStatement updatePs = connection.prepareStatement("UPDATE BET_HISTORY SET RESULTS = " +
                    "?" +
                    ", STAGE = " + "? " +
                    ", LAST_UPDATE = " + "? " +
                    ", NOTES = ? " +
                    "WHERE TITLE = ? and SPORT = ? and BET_TIME > (now() - INTERVAL 300 minute)"
            );

            for (BetItem item : betItems) {
                if (!existingBets.containsKey(item.getTitle() + item.getSport()) ||
                        (existingBets.containsKey(item.getTitle() + item.getSport()) &&
                                existingBets.get(item.getTitle() + item.getSport()).getDate().getTime()<within27H )) {
                    String stage = item.getStage();
                    if(item.getTitle().toLowerCase().contains("u-1") || item.getTitle().toLowerCase().contains("u-2")
                            || item.getTitle().toLowerCase().contains("youth")){
                        continue;
                    }
                    if(Math.min(item.getResults().getFirst().getCoef1(),item.getResults().getFirst().getCoef2()) >= Constants.BET_LIMIT
                            || Math.max(item.getResults().getFirst().getCoef1(),item.getResults().getFirst().getCoef2())<1 ){
                        //skip items with coef1 or coef2 > BET_LIMIT
                        //and
                        //skip items with 0.0 coefs - no data yet
                        continue;
                    }
                    if(item.getResults().getFirst().getResult().matches(".*[123456789].*")){
                        //skip items, which started already
                        continue;
                    }

                    if(item.getResults().size() > 0 && item.getResults().getLast().getCoef1() > 1 &&
                            item.getResults().getLast().getCoef2() > 1){
                        if ( item.getResults().getLast().getCoef1() < 1.05) {
                            logger.info("set player1:1 " + item.getTitle() + ":" +item.getResults().getLast().toString());
                            stage = "player1:1";
                        }
                        if ( item.getResults().getLast().getCoef2() < 1.05) {
                            stage = "player2:1";
                            logger.info("set player2:1 " + item.getTitle() + ":" +item.getResults().getLast().toString());
                        }
                    }

                    insertPs.setString(1, item.getTitle().replaceAll("'", ""));
                    insertPs.setString(2, item.getSport());
                    insertPs.setString(3, JsonMapper.mapper.writeValueAsString(item.getResults()));
                    insertPs.setDate(4, sqlDateNow);
                    insertPs.setString(5, stage);
                    insertPs.setString(6, item.getLink());
                    insertPs.setString(7, item.getCompetition());
                    insertPs.setString(8, item.getLiveStream());
                    insertPs.addBatch();
                    logger.info("Insert: " + item.getTitle() );
                } else {

                    BetItem existingItem = existingBets.get(item.getTitle() + item.getSport());
                    LinkedList<MomentResult> existingResults = existingItem.getResults();

                    if(!existingResults.isEmpty() && !item.getResults().isEmpty()){
                        if(!existingResults.getLast().equals(item.getResults().getFirst())){
                            existingResults.addAll(item.getResults());
                        }
                    } else {
                        existingResults.addAll(item.getResults());
                    }

                    String stage = existingItem.getStage();
                    String notes = existingItem.getNotes() == null ? "" : existingItem.getNotes();
                    //zero stage
                    if(existingResults.getLast().getCoef1()>0 && existingResults.getLast().getCoef2()>0 ){
                        if (stage != null && stage.contains("0")) {
                            if (item.getResults().size() > 0 && existingResults.getLast().getCoef1() < Constants.FIRST_STAGE_COEF) {
                                logger.info("set player1:1 " + item.getTitle() + ":" +item.getResults().getLast().toString());
                                stage = "player1:1";
                            }
                            if (item.getResults().size() > 0 && existingResults.getLast().getCoef2() < Constants.FIRST_STAGE_COEF) {
                                stage = "player2:1";
                                logger.info("set player2:1 " + item.getTitle() + ":" +item.getResults().getLast().toString());
                            }
                        }
                        if (stage != null && stage.contains("player1:1")) {
                            if (item.getResults().size() > 0 && existingResults.getLast().getCoef1() >= Constants.SECOND_STAGE_COEF) {
                                stage = "player1:2";
                                logger.info("set player1:2 " + item.getTitle() + ":" +item.getResults().getLast().toString());
                            }
                        }
                        if (stage != null && stage.contains("player2:1")) {
                            if (item.getResults().size() > 0 && existingResults.getLast().getCoef2() >= Constants.SECOND_STAGE_COEF) {
                                stage = "player2:2";
                                logger.info("set player2:2 " + item.getTitle() + ":" +item.getResults().getLast().toString());
                            }
                        }

                        if (stage != null && stage.contains("player1:2")) {
                            if (item.getResults().size() > 0 && existingResults.getLast().getCoef1() <= Constants.THIRD_STAGE_COEF) {
                                stage = "player1:3";
                                logger.info("set player1:3 " + item.getTitle() + ":" +item.getResults().getLast().toString());
                            }
                        }
                        if (stage != null && stage.contains("player2:2")) {
                            if (item.getResults().size() > 0 && existingResults.getLast().getCoef2() <= Constants.THIRD_STAGE_COEF) {
                                stage = "player2:3";
                                logger.info("set player2:3 " + item.getTitle() + ":" +item.getResults().getLast().toString());
                            }
                        }

                        if (stage != null && stage.contains("player1:3")) {
                            if (item.getResults().size() > 0 && existingResults.getLast().getCoef1() > Constants.FOURTH_STAGE_COEF) {
                                stage = "player1:4";
                                logger.info("set player1:4 " + item.getTitle() + ":" +item.getResults().getLast().toString());
                            }
                        }
                        if (stage != null && stage.contains("player2:3")) {
                            if (item.getResults().size() > 0 && existingResults.getLast().getCoef2() > Constants.FOURTH_STAGE_COEF) {
                                stage = "player2:4";
                                logger.info("set player2:4 " + item.getTitle() + ":" +item.getResults().getLast().toString());
                            }
                        }

                        if (stage != null && stage.contains("player1:4")) {
                            if (item.getResults().size() > 0 && existingResults.getLast().getCoef1() <= Constants.FIFTH_STAGE_COEF) {
                                stage = "player1:5";
                                logger.info("set player1:5 " + item.getTitle() + ":" +item.getResults().getLast().toString());
                            }
                        }
                        if (stage != null && stage.contains("player2:4")) {
                            if (item.getResults().size() > 0 && existingResults.getLast().getCoef2() <= Constants.FIFTH_STAGE_COEF) {
                                stage = "player2:5";
                                logger.info("set player2:5 " + item.getTitle() + ":" +item.getResults().getLast().toString());
                            }
                        }
                    }

                    // comeback settings
                    if (stage != null ) {
                        item.setNotes(notes);
                        if (stage.contains("player1") && !notes.contains(Constants.RETURNS_TO_BET_BOUND)) {
                            if (notes.contains(Constants.PLAYER1_CROLL_LIMIT)) {
                                if (existingResults.getLast().getCoef1() >= Constants.COMEBACK_PERFORM_BET_BOUND[0] &&
                                        existingResults.getLast().getCoef1() <= Constants.COMEBACK_PERFORM_BET_BOUND[1]) {
                                    item.setNotes(Constants.PLAYER1_CROLL_LIMIT + ":" + Constants.RETURNS_TO_BET_BOUND);
                                }
                            }
                            if (existingResults.getLast().getCoef1() >= Constants.COMEBACK_LIMIT &&
                                    !item.getNotes().contains(Constants.PLAYER1_CROLL_LIMIT)) {
                                item.setNotes(Constants.PLAYER1_CROLL_LIMIT);
                            }
                        } else if (stage.contains("player2") && !notes.contains(Constants.RETURNS_TO_BET_BOUND)) {
                            if (notes.contains(Constants.PLAYER2_CROLL_LIMIT)) {
                                if (existingResults.getLast().getCoef2() >= Constants.COMEBACK_PERFORM_BET_BOUND[0] &&
                                        existingResults.getLast().getCoef2() <= Constants.COMEBACK_PERFORM_BET_BOUND[1]) {
                                    item.setNotes(Constants.PLAYER2_CROLL_LIMIT + ":" + Constants.RETURNS_TO_BET_BOUND);
                                }
                            }
                            if (existingResults.getLast().getCoef2() >= Constants.COMEBACK_LIMIT
                                    && !item.getNotes().contains(Constants.PLAYER2_CROLL_LIMIT)) {
                                item.setNotes(Constants.PLAYER2_CROLL_LIMIT);
                            }
                        }
                    }

                    updatePs.setString(1, JsonMapper.mapper.writeValueAsString(existingResults));
                    updatePs.setString(2, stage);
                    updatePs.setObject(3, sqlDateTimeNow);
                    updatePs.setString(4, item.getNotes());
                    updatePs.setString(5, item.getTitle());
                    updatePs.setString(6, item.getSport());
                    updatePs.executeUpdate();
                }
            }
            insertPs.executeBatch();

            insertPs.close();
            updatePs.close();
            connection.close();
        } catch (SQLException e) {
            logger.info(e.getMessage() + ":;;");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    @Override
    public LinkedList<BetItem> getStage5Items() {
        LinkedList<BetItem> result = new LinkedList<>();
        try {
            Connection connection = ConnectionFactory.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM BET_HISTORY" +
                    " WHERE STAGE like '%:5%' AND STAGE NOT LIKE '%COMPLETED%' AND STAGE NOT LIKE '%ERROR%'");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                //here we dont need RESULTS, because bet is already in 3rd stage
                BetItem bet = new BetItem(rs.getString("TITLE"),rs.getString("SPORT"),null, rs.getString("STAGE"), "", rs.getString("NOTES"), rs.getString("COMPETITION"));
                result.add(bet);
            }
            connection.close();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return result;
    }

    @Override
    public List<BetItem> getBetsWhereComebackIsPossile() {
        List<BetItem> result = new ArrayList<>();
        try {
            Connection connection = ConnectionFactory.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM BET_HISTORY" +
                    " WHERE NOTES like '%" +
                    Constants.RETURNS_TO_BET_BOUND +
                    "%' AND LAST_UPDATE > now() - INTERVAL 100 SECOND");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                //here we dont need RESULTS, because bet is already in 3rd stage
                BetItem bet = new BetItem(rs.getString("TITLE"),rs.getString("SPORT"),null, rs.getString("STAGE"), "", rs.getString("NOTES"), rs.getString("COMPETITION"));
                result.add(bet);
            }
            connection.close();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        return result;
    }

    @Override
    public void markBetAsCompleted(BetItem bet) {
        try {
            Connection connection = ConnectionFactory.getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE BET_HISTORY" +
                    " SET STAGE = ?, NOTES = ? WHERE (TITLE LIKE ? or TITLE LIKE ?) and LAST_UPDATE > now() - INTERVAL 1000 SECOND ");
            logger.info("Marking as completed:" + bet.getStage() + bet.getTitle() +"->" + bet.getNotes());
            ps.setString(1, bet.getStage());
            ps.setString(2, bet.getNotes());
//            ps.setString(3, bet.getTitle());//old way
            String[] titleParts = bet.getTitle().split("-");
            ps.setString(3, "%" + titleParts[0].trim() + "%");
            ps.setString(4, "%" + titleParts[1].trim() + "%");

            int affectedRows = ps.executeUpdate();
            logger.info("Marked as completed:(affected=" + affectedRows +")"
                    + bet.getStage() + bet.getTitle() +"->" + bet.getNotes());

            try {
                    String text = LocalDateTime.now().toString() + "\n";
                    text += ps.toString()+"\n";
                    text+= "Marked as completed:(affected=" + affectedRows +")"
                            + bet.getStage() + bet.getTitle() +"->" + bet.getNotes() + "[" +titleParts[0].trim()+","
                            + titleParts[1].trim() +"]";
                    text+="\n ---------------\n";
                    FileUtils.write(new File("/home/andrii/update_log.txt"),text, "UTF-8", true);


            }catch (IOException e){
                e.printStackTrace();
            }


            connection.close();
        }catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    @Override
    public void updateCurrentBetStatus(Integer winLastBet, Double currentAmount, Double lastLoseBetsSum,
                                       String lastBetTitle) {
        try {
            Connection connection = ConnectionFactory.getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE CURRENT_BET_STATUS" +
                    " SET WIN_LAST_BET = ?, CURRENT_AMOUNT = ?, LAST_LOSE_BETS_SUM = ?, " +
                    "LAST_BET_TITLE = ?  WHERE ID = 1");
            ps.setInt(1, winLastBet);
            ps.setDouble(2, currentAmount);
            ps.setDouble(3, lastLoseBetsSum);
            ps.setString(4, lastBetTitle);

            logger.info("Updated CURRENT_BET_STATUS:" + winLastBet + " " + currentAmount + " "+
            lastBetTitle );
            ps.executeUpdate();
            connection.close();
        }catch (Exception e){
            logger.info(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getLastPerformedBet() {
        Map<String, Object> results = new HashMap<>();
        results.put("WIN_LAST_BET", 1000);
        results.put("LAST_BET_TITLE", "");
        try {
            Connection connection = ConnectionFactory.getConnection();//
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT " +
                    "WIN_LAST_BET, CURRENT_AMOUNT, LAST_LOSE_BETS_SUM, LAST_BET_TITLE " +
                    " FROM CURRENT_BET_STATUS WHERE ID=1");
            resultSet.next();
            results.put("WIN_LAST_BET", resultSet.getInt(1));
            results.put("CURRENT_AMOUNT", resultSet.getDouble(2));
            results.put("LAST_LOSE_BETS_SUM", resultSet.getDouble(3));
            results.put("LAST_BET_TITLE", resultSet.getString(4));
            connection.close();
        } catch (Exception e){
            logger.info(e.getMessage());
        }
        return results;
    }

    @Override
    public Double getLastLoseBetsSum() {
        Double result = null; // N/A
        try {
            Connection connection = ConnectionFactory.getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT LAST_LOSE_BETS_SUM " +
                    " FROM CURRENT_BET_STATUS WHERE ID=1");
            resultSet.next();
            result = resultSet.getDouble(1);
            connection.close();
            return result;
        }catch (Exception e){
            logger.info(e.getMessage());
        }
        return result;
    }

    @Override
    public List<String> getLastBetInfo() {
        List<String> result = new ArrayList<>();
        try {
            Connection connection = ConnectionFactory.getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * " +
                    " FROM CURRENT_BET_STATUS WHERE ID=1");
            resultSet.next();
            result.add(resultSet.getInt(1)+"");
            result.add(resultSet.getString(2)+"");
            result.add(resultSet.getString(3)+"");
            result.add(resultSet.getString(4)+"");
            connection.close();
            return result;
        }catch (Exception e){
            logger.info(e.getMessage());
        }
        return result;
    }

    @Override
    public String stagesCount() {
        //String sql = "SELECT substr(stage, instr(STAGE,':')+1, 1) , count(*) FROM BET_HISTORY WHERE DATE = current_date  " +
        //        "and LAST_UPDATE is not null GROUP BY  substr(stage, instr(STAGE,':')+1, 1)";
        String sql = "SELECT substr(stage, instr(STAGE,':')+1, 1), TITLE,date FROM BET_HISTORY WHERE DATE = current_date order by last_update";
        String result = "";

        try {
            Connection connection = ConnectionFactory.getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            while (resultSet.next()){
                result += resultSet.getString(1);
            }

        }catch (Exception e){
            logger.info(e.getMessage());
        }

        int count24 = StringUtils.countMatches(result, "2") + StringUtils.countMatches(result, "4");
        int count135 = StringUtils.countMatches(result, "1") + StringUtils.countMatches(result, "3")
            + StringUtils.countMatches(result, "5");
        int totalCount = result.length();
        double percent24 = (double) count24 / totalCount;

        return String.format("%s (count = %d, '24'= %d, '135' = %d, '24 percent' = %f", result,
                totalCount, count24, count135, percent24);
    }

    @Override
    public String comebackItemsAndTheirResultsAsHtml(Boolean onlyCurrDate) {
        //String sql = "SELECT substr(stage, instr(STAGE,':')+1, 1) , count(*) FROM BET_HISTORY WHERE DATE = current_date  " +
        //        "and LAST_UPDATE is not null GROUP BY  substr(stage, instr(STAGE,':')+1, 1)";
        String sql = "SELECT * FROM BET_HISTORY WHERE NOTES LIKE '%RETU%' AND DATE = current_date order by ID";
        if(!onlyCurrDate){
             sql = "SELECT * FROM BET_HISTORY WHERE  NOTES LIKE '%RETU%' AND DATE >= '2018-01-15' order by last_update desc";
        }

        String result = "";
        List<String> lastBetInfo = getLastBetInfo();
        result += "Current money:$" + lastBetInfo.get(2)+ "$<br/>";
        int counter = 1;
        try {
            Connection connection = ConnectionFactory.getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            while (resultSet.next()){
                LinkedList<MomentResult> results = objectMapper.readValue(resultSet.getString("RESULTS"), new TypeReference<LinkedList<MomentResult>>() {
                });
                WinChecker winChecker = WinCheckerProvider.getWinChecker(resultSet.getString("SPORT"));

                String sportCompetition =  resultSet.getString("COMPETITION") != null ? resultSet.getString("COMPETITION"):
                        resultSet.getString("SPORT");
                Integer id = resultSet.getInt("ID");
                String stage = resultSet.getString("STAGE");

                Double max1 = results.stream().map(MomentResult::getCoef1).max(Comparator.comparing(Double::doubleValue)).get();
                Double max2 = results.stream().map(MomentResult::getCoef2).max(Comparator.comparing(Double::doubleValue)).get();

                String item = counter++ + "(" + id +
                        ") [" + SystemUtils.getPcName() +"] [" + stage + "] [live stream: " + resultSet.getString("LIVE_STREAM") +"]" + sportCompetition  + "[" +
                        resultSet.getString("BET_TIME") +"] - "
                        + resultSet.getString("TITLE")  + " <b>"+ resultSet.getString("NOTES") + "</b> "
                        + "[" + results.getFirst().getCoef1() + ", " + results.getFirst().getCoef2() + "] "
                        + "[max coefs: " + max1 + "," + max2 + "]"
                        +  results.getLast().getResult()
                        + "[lastupdate=" + resultSet.getString("LAST_UPDATE") +"]";

                if(winChecker != null){
                    int winner = winChecker.getWinner(results.getLast().getResult());

                    if(winner != -1){
                        if(resultSet.getString("NOTES").contains(""+winner) ){
                            item ="<span style='color:green'>" + item;
                        } else {
                            item="<span style='color:red'>" + item;
                        }
                        item += "</span>";
                    }
                }
                result = result + item + "<br/> <br/>";
            }

        }catch (Exception e){
            logger.info(e.getMessage());
        }

        return result;
    }

    @Override
    public LinkedList<Pair<Integer, BetItem>> getAllComebackItemsFromHistory(int count) {
        String sql = "SELECT * FROM BET_HISTORY WHERE NOTES LIKE '%RETU%' AND DATE >= '2018-01-15' and  LAST_UPDATE < now() - INTERVAL 120 second " +
                "order by ID desc";

        return getItemsAndWinnersPairs(count, sql);
    }

    @Override
    public LinkedList<Pair<Integer, BetItem>> getItemsFromHistory(int count) {
        String sql = "SELECT * FROM BET_HISTORY WHERE  DATE >= '2018-01-15' and  LAST_UPDATE < now() - INTERVAL 100 second " +
                "order by ID desc";
        return getItemsAndWinnersPairs(count, sql);
    }

    private LinkedList<Pair<Integer, BetItem>> getItemsAndWinnersPairs(int count, String sql) {
        LinkedList<Pair<Integer, BetItem>> res = new LinkedList<>();
        int counter = 0;
        String result = "";
        Connection connection = null;
        try {
            connection = ConnectionFactory.getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            while (resultSet.next()) {
                LinkedList<MomentResult> results = objectMapper.readValue(resultSet.getString("RESULTS"), new TypeReference<LinkedList<MomentResult>>() {
                });
                WinChecker winChecker = WinCheckerProvider.getWinChecker(resultSet.getString("SPORT"));

                String sportCompetition = resultSet.getString("COMPETITION") != null ? resultSet.getString("COMPETITION") :
                        resultSet.getString("SPORT");

                BetItem betItem = new BetItem(resultSet.getString("TITLE"), resultSet.getString("SPORT"), results, resultSet.getString("STAGE"), resultSet.getString("LINK"),
                        resultSet.getString("NOTES"), sportCompetition);
                betItem.setDate(resultSet.getDate("DATE"));
                counter++;

                if (winChecker != null) {
                    int winner = winChecker.getWinner(results.getLast().getResult());
                    res.add(Pair.of(winner, betItem));
                }

                if (counter >= count) {
                    break;
                }
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    @Override
    public List<String> getPlayerStagesFromHistory(String title, String sport) {
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
            logger.info(e.getMessage());
        }
        return result;
    }

    @Override
    public BetItem getRandomFavourite(int minBetFavItems) {
        List<BetItem> result = new ArrayList<>();
        try {
            Connection connection = ConnectionFactory.getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM BET_HISTORY" +
                    " WHERE (STAGE like '%:1%' or stage like '%:3%')  AND LAST_UPDATE > now() - INTERVAL 100 SECOND");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BetItem bet = new BetItem(rs.getString("TITLE"),rs.getString("SPORT"),
                        JsonMapper.mapper.readValue(rs.getString("RESULTS"), new TypeReference<LinkedList<MomentResult>>() {
                        }), rs.getString("STAGE"), "", rs.getString("NOTES"), rs.getString("COMPETITION"));
                if(bet.getResults().getLast().getCoef1() > 0 &&
                        Math.max(bet.getResults().getLast().getCoef1(), bet.getResults().getLast().getCoef2()) > 1.05
                        ){
                    result.add(bet);
                }
            }
            connection.close();
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        if(!result.isEmpty() && minBetFavItems <= result.size()){
            int randomInt = random.nextInt(result.size());
            return result.get(randomInt);
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        BetRepo b =new BetRepoJdbc();
        //System.out.println(b.getPlayerStagesFromHistory("Tianjin - Bayi", "Volleyball"));
        System.out.println(b.getRandomFavourite(4));
        System.out.println(new ArrayList<>(null));
    }


}