package com.ap;

import com.ap.dao.ConnectionFactory;
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

/**
 * Hello world!
 *
 */
public class _1CoefAnalysisLessThenSomeNumber
{
    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    public static void main( String[] args ) throws SQLException, IOException {
        System.out.println( "Hello World!" );
        Connection connection = ConnectionFactory.getConnection();
        Statement statement = connection.createStatement();
        int counter = 0;
        int less1_17AtStartCounter = 0;
        ResultSet result = statement.executeQuery("select TITLE,SPORT,STAGE,BET_TIME, RESULTS from BET_HISTORY where DATE >= '2017-09-23' and " +
                "( STAGE like '%:5COMPLETED%')  order by BET_TIME desc;\n");

        while (result.next()){
            System.out.println(++counter);
            String stage = result.getString(3);
            System.out.println(result.getString(1));
             LinkedList<MomentResult> results = objectMapper.readValue(result.getString(5), new TypeReference<LinkedList<MomentResult>>() {

             });
             if(stage.contains("1")){
                 System.out.println(results.getFirst().getCoef1());
                if(results.getFirst().getCoef1()<1.3){
                    less1_17AtStartCounter++;
                    System.out.println("here");
                }
             }else {
                 System.out.println(results.getFirst().getCoef2());
                 if(results.getFirst().getCoef2()<1.3){
                     less1_17AtStartCounter++;
                     System.out.println("here");
                 }
             }
            System.out.println("---------------------");
        }

        System.out.println("LEss then 1.17 count=" + less1_17AtStartCounter + "( of "+ counter+" bets)");
        statement.close();
        connection.close();
    }
}
