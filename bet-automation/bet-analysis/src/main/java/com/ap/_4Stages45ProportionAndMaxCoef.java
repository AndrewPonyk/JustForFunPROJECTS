package com.ap;

import com.ap.model.MomentResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class _4Stages45ProportionAndMaxCoef {
    private static String sql = "select * from BET_HISTORY where (stage like '%:4%' or " +
            " stage like '%:5%') AND DATE >'2017-10-26'";
    public static ObjectMapper objectMapper = new ObjectMapper();


    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static void main(String[] args) throws SQLException, IOException {

        Connection connection = ConnectionFactory.getConnection();
        ResultSet resultSet = connection.createStatement().executeQuery(sql);
        int counter = 0;
        while (resultSet.next()){
            Double max = 0.0;
            counter++;
            String stage = resultSet.getString("STAGE");
            String results = resultSet.getString("RESULTS");
            int player = stage.contains("player1") ? 1 : 2;

            LinkedList<MomentResult> resultsList = objectMapper.readValue(results, new TypeReference<LinkedList<MomentResult>>() {
            });
            for (MomentResult item : resultsList) {
                max = Math.max(max, player == 1 ? item.getCoef1() : item.getCoef2());
            }
            if(max>3.0)
            System.out.println(String.format("%-40s %f %50s", stage, max, resultsList.getLast().toString()));
        }
        System.out.println(counter);
    }
}
