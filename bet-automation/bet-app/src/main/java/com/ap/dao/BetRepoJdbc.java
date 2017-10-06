package com.ap.dao;

import com.ap.model.BetItem;
import com.ap.model.JsonMapper;
import com.ap.model.MomentResult;
import com.ap.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class BetRepoJdbc implements BetRepo {
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
        java.util.Date yesterday = new java.util.Date();
        Long within24H = yesterday.getTime() - (24*60*60*1000L);

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
                    betCondition);
//            System.out.println("----------sss");
//            System.err.println("select * from BET_HISTORY where concat(TITLE,'', SPORT)  in " +
//                    betCondition);
//            System.out.println("----------end");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BetItem item = new BetItem(rs.getString("TITLE"), rs.getString("SPORT"),
                        JsonMapper.mapper.readValue(rs.getString("RESULTS"), new TypeReference<LinkedList<MomentResult>>() {
                        }), rs.getString("STAGE"));
                item.setDate(rs.getDate("DATE"));
                existingBets.put(item.getTitle() + item.getSport(), item);
            }
            PreparedStatement insertPs = connection.prepareStatement("INSERT INTO BET_HISTORY (TITLE, SPORT, RESULTS, DATE, STAGE)" +
                    " VALUES (?, ?, ?, ?, ?)");
            PreparedStatement updatePs = connection.prepareStatement("UPDATE BET_HISTORY SET RESULTS = " +
                    "?" +
                    ", STAGE = " + "? " +
                    "WHERE TITLE = ? and SPORT = ?"
            );

            for (BetItem item : betItems) {
                if (!existingBets.containsKey(item.getTitle() + item.getSport()) ||
                        (existingBets.containsKey(item.getTitle() + item.getSport()) &&
                                existingBets.get(item.getTitle() + item.getSport()).getDate().getTime()<within24H )) {
                    String stage = item.getStage();
                    if (item.getResults().size() > 0 && item.getResults().getLast().getCoef1() < 1.05) {
                        System.out.println("set player1:1" + item.getResults().getLast().getCoef1());
                        stage = "player1:1";
                    }
                    if (item.getResults().size() > 0 && item.getResults().getLast().getCoef2() < 1.05) {
                        stage = "player2:1";
                    }
                    insertPs.setString(1, item.getTitle().replaceAll("'", ""));
                    insertPs.setString(2, item.getSport());
                    insertPs.setString(3, JsonMapper.mapper.writeValueAsString(item.getResults()));
                    insertPs.setDate(4, new Date(Calendar.getInstance().getTime().getTime()));
                    insertPs.setString(5, stage);
                    insertPs.addBatch();
                    System.out.println("Insert: " + item.getTitle() );
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
                    //zero stage
                    if (stage != null && stage.contains("0")) {
                        if (item.getResults().size() > 0 && existingResults.getLast().getCoef1() < Constants.FIRST_STAGE_COEF) {
                            System.out.println("set player1:1" + item.getResults().getLast().getCoef1());
                            stage = "player1:1";
                        }
                        if (item.getResults().size() > 0 && existingResults.getLast().getCoef2() < Constants.FIRST_STAGE_COEF) {
                            stage = "player2:1";
                        }
                    }
                    if (stage != null && stage.contains("player1:1")) {
                        if (item.getResults().size() > 0 && existingResults.getLast().getCoef1() >= Constants.SECOND_STAGE_COEF) {
                            stage = "player1:2";
                        }
                    }
                    if (stage != null && stage.contains("player2:1")) {
                        if (item.getResults().size() > 0 && existingResults.getLast().getCoef2() >= Constants.SECOND_STAGE_COEF) {
                            stage = "player2:2";
                        }
                    }

                    if (stage != null && stage.contains("player1:2")) {
                        if (item.getResults().size() > 0 && existingResults.getLast().getCoef1() <= Constants.THIRD_STAGE_COEF) {
                            stage = "player1:3";
                        }
                    }
                    if (stage != null && stage.contains("player2:2")) {
                        if (item.getResults().size() > 0 && existingResults.getLast().getCoef2() <= Constants.THIRD_STAGE_COEF) {
                            stage = "player2:3";
                        }
                    }

                    if (stage != null && stage.contains("player1:3")) {
                        if (item.getResults().size() > 0 && existingResults.getLast().getCoef1() > Constants.FOURTH_STAGE_COEF) {
                            stage = "player1:4";
                        }
                    }
                    if (stage != null && stage.contains("player2:3")) {
                        if (item.getResults().size() > 0 && existingResults.getLast().getCoef2() > Constants.FOURTH_STAGE_COEF) {
                            stage = "player2:4";
                        }
                    }

                    if (stage != null && stage.contains("player1:4")) {
                        if (item.getResults().size() > 0 && existingResults.getLast().getCoef1() <= Constants.FIFTH_STAGE_COEF) {
                            stage = "player1:5";
                        }
                    }
                    if (stage != null && stage.contains("player2:4")) {
                        if (item.getResults().size() > 0 && existingResults.getLast().getCoef2() <= Constants.FIFTH_STAGE_COEF) {
                            stage = "player2:5";
                        }
                    }

                    ////////////////////

                    updatePs.setString(1, JsonMapper.mapper.writeValueAsString(existingResults));
                    updatePs.setString(2, stage);
                    updatePs.setString(3, item.getTitle());
                    updatePs.setString(4, item.getSport());
                    //System.out.println("update " + item.getTitle() + "->" + stage + " Results size:" + existingResults.size() + ":" + existingResults.getLast());
                    //System.out.println("========");
                    updatePs.executeUpdate();
                }
            }
            insertPs.executeBatch();

            insertPs.close();
            updatePs.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage() + ":;;");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
                BetItem bet = new BetItem(rs.getString("TITLE"),rs.getString("SPORT"),null, rs.getString("STAGE"));
                result.add(bet);
            }
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
            System.out.println("Marking as completed:" + bet.getStage() + bet.getTitle());
            ps.executeUpdate();
            connection.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
