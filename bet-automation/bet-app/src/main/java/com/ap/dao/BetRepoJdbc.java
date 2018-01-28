package com.ap.dao;

import com.ap.model.BetItem;
import com.ap.model.JsonMapper;
import com.ap.model.MomentResult;
import com.ap.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class BetRepoJdbc implements BetRepo {

    Logger logger = LoggerFactory.getLogger(BetRepoJdbc.class);
    Random random = new Random();

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
                        }), rs.getString("STAGE"), "", rs.getString("NOTES"));
                item.setDate(rs.getDate("DATE"));
                existingBets.put(item.getTitle() + item.getSport(), item);
            }
            PreparedStatement insertPs = connection.prepareStatement("INSERT INTO BET_HISTORY (TITLE, SPORT, RESULTS, DATE, STAGE, LINK)" +
                    " VALUES (?, ?, ?, ?, ?, ?)");
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
                    if (stage != null) {
                        item.setNotes(notes);
                        if (stage.contains("player1")) {
                            if (notes.contains(Constants.PLAYER1_CROLL_LIMIT)) {
                                if (existingResults.getLast().getCoef1() >= Constants.COMEBACK_PERFORM_BET_BOUND[0] &&
                                        existingResults.getLast().getCoef1() <= Constants.COMEBACK_PERFORM_BET_BOUND[1]) {
                                    item.setNotes(Constants.PLAYER1_CROLL_LIMIT + ":" + Constants.RETURNS_TO_BET_BOUND);
                                }
                            }
                            if (existingResults.getLast().getCoef1() > Constants.COMEBACK_LIMIT &&
                                    !item.getNotes().contains(Constants.PLAYER1_CROLL_LIMIT)) {
                                item.setNotes(Constants.PLAYER1_CROLL_LIMIT);
                            }
                        } else if (stage.contains("player2")) {
                            if (notes.contains(Constants.PLAYER2_CROLL_LIMIT)) {
                                if (existingResults.getLast().getCoef2() >= Constants.COMEBACK_PERFORM_BET_BOUND[0] &&
                                        existingResults.getLast().getCoef2() <= Constants.COMEBACK_PERFORM_BET_BOUND[1]) {
                                    item.setNotes(Constants.PLAYER2_CROLL_LIMIT + ":" + Constants.RETURNS_TO_BET_BOUND);
                                }
                            }
                            if (existingResults.getLast().getCoef2() > Constants.COMEBACK_LIMIT
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
                BetItem bet = new BetItem(rs.getString("TITLE"),rs.getString("SPORT"),null, rs.getString("STAGE"), "", rs.getString("NOTES"));
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
                    "%' AND LAST_UPDATE > now() - INTERVAL 120 SECOND");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                //here we dont need RESULTS, because bet is already in 3rd stage
                BetItem bet = new BetItem(rs.getString("TITLE"),rs.getString("SPORT"),null, rs.getString("STAGE"), "", rs.getString("NOTES"));
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
                    " SET STAGE = ? WHERE TITLE = ?");
            ps.setString(1, bet.getStage());
            ps.setString(2, bet.getTitle());
            logger.info("Marking as completed:" + bet.getStage() + bet.getTitle());
            ps.executeUpdate();
            connection.close();
        }catch (Exception e){
            logger.info(e.getMessage());
        }
    }

    @Override
    public void updateCurrentBetStatus(Integer winLastBet, Double currentAmount, Double lastLoseBetsSum) {
        try {
            Connection connection = ConnectionFactory.getConnection();
            PreparedStatement ps = connection.prepareStatement("UPDATE CURRENT_BET_STATUS" +
                    " SET WIN_LAST_BET = ?, CURRENT_AMOUNT = ?, LAST_LOSE_BETS_SUM = ?  WHERE ID = 1");
            ps.setInt(1, winLastBet);
            ps.setDouble(2, currentAmount);
            ps.setDouble(3, lastLoseBetsSum);

            logger.info("Updated CURRENT_BET_STATUS:" + winLastBet + " " + currentAmount);
            ps.executeUpdate();
            connection.close();
        }catch (Exception e){
            logger.info(e.getMessage());
        }
    }

    @Override
    public Integer getLastBetStatus() {
        Integer result = -1000; // N/A
        try {
            Connection connection = ConnectionFactory.getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT WIN_LAST_BET " +
                    " FROM CURRENT_BET_STATUS WHERE ID=1");
            resultSet.next();
            result = resultSet.getInt(1);
            connection.close();
            return result;
        }catch (Exception e){
            logger.info(e.getMessage());
        }
        return result;
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
                //result += resultSet.getString(1) +":"+resultSet.getString(2)+"<br/>";
            }

        }catch (Exception e){
            logger.info(e.getMessage());
        }
        return result;
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
                        }), rs.getString("STAGE"), "", rs.getString("NOTES"));
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