package com.ap.visualization;


import com.ap.ConnectionFactory;
import com.ap.model.MomentResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BetDataPlotRetriever {
    private final static String SELECT_STRING = "SELECT * from BET_HISTORY where id=";
    private final static String SELECT_TITLE_LIKE_STRING = "SELECT * from BET_HISTORY where title like '%s' order by id desc";

    private final static String SELECT_ALL_BETS_FROM_DATE = "SELECT * from BET_HISTORY where DATE >='%s' order by id desc";
    private static final String SELECT_ALL_DATA = "Select * from BET_HISTORY";
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static Optional<List<ImmutableTriple<String, Double, Double>>> getBetDataByLike(String like) throws SQLException, IOException {
        ResultSet resultSet = getResultSet(String.format(SELECT_TITLE_LIKE_STRING, like) );
        return resultSetToTriplesResult(resultSet);
    }

    public static Optional<List<ImmutableTriple<String, Double, Double>>> getBetData(String id) throws SQLException, IOException {
        ResultSet resultSet = getResultSet(SELECT_STRING + id);
        return resultSetToTriplesResult(resultSet);
    }

    public static List<List<ImmutableTriple<String, Double, Double>>> getAllBetsFromDate(String date) throws SQLException, IOException {
        List<List<ImmutableTriple<String, Double, Double>>> result = new ArrayList<>();

        ResultSet resultSet = getResultSet(String.format(SELECT_ALL_BETS_FROM_DATE, date));
        Optional<List<ImmutableTriple<String, Double, Double>>> bet = resultSetToTriplesResult(resultSet);
        while (bet.isPresent()){
            result.add(bet.get());
            bet = resultSetToTriplesResult(resultSet);
        }

        return result;
    }

    public static List<List<ImmutableTriple<String, Double, Double>>> getAllBetsFromQuery(String condition) throws SQLException, IOException {
        List<List<ImmutableTriple<String, Double, Double>>> result = new ArrayList<>();

        ResultSet resultSet = getResultSet(SELECT_ALL_DATA + " WHERE " + condition);
        Optional<List<ImmutableTriple<String, Double, Double>>> bet = resultSetToTriplesResult(resultSet);
        while (bet.isPresent()){
            result.add(bet.get());
            bet = resultSetToTriplesResult(resultSet);
        }

        return result;
    }


    private static Optional<List<ImmutableTriple<String, Double, Double>>> resultSetToTriplesResult(ResultSet resultSet) throws SQLException, IOException {
        Optional<List<ImmutableTriple<String, Double, Double>>> result = Optional.empty();
        if(resultSet.next()){

            String results = resultSet.getString("RESULTS");
            LinkedList<MomentResult> resultsList = objectMapper.readValue(results, new TypeReference<LinkedList<MomentResult>>() {
            });
            String title = resultSet.getString("TITLE")
                    + " Sport:" + resultSet.getString("SPORT") +"; Last Result:"
                    + resultsList.getLast();
            List<ImmutableTriple<String, Double, Double>> collect = resultsList.stream().map(item -> {
                return new ImmutableTriple<String, Double, Double>(title, item.getCoef1(), item.getCoef2());
            }).collect(Collectors.toList());
            result = Optional.of(collect);
        }

        return result;
    }

    private static ResultSet getResultSet(String query) throws SQLException {
        Connection connection = ConnectionFactory.getConnection();
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }
}
