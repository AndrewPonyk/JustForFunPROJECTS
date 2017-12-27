package com.ap.sync;

import com.ap.ConnectionFactory;

import java.sql.*;
import java.util.Scanner;

public class SyncBetHistory {
    public static Scanner scanner = new Scanner(System.in);

    //localhost:root:root
    //35.196.157.109:root:root1992
    public static void main(String[] args) throws SQLException {
        String[] serverDb = "localhost:root:root".split(":");
        String[] remoteDb = "35.196.22.212:root:root1992".split(":");

        System.out.println("Copy local to remote press '0', remote to local - '1'");
        String input = scanner.nextLine();

        Connection localConnection = ConnectionFactory.getConnection(serverDb[0], serverDb[1], serverDb[2]);
        Connection remoteConnection = ConnectionFactory.getConnection(remoteDb[0], remoteDb[1], remoteDb[2]);

        if("0".equals(input)){
            syncBetHistory(localConnection, remoteConnection);
        } else if("1".equals(input)){
            syncBetHistory(remoteConnection, localConnection);
        }

    }

    public static void syncBetHistory(Connection connection, Connection remoteConnection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from BET_HISTORY");
        String sqlInsert = "INSERT INTO BET_HISTORY (TITLE,SPORT,RESULTS,DATE,STAGE,BET_TIME,LAST_UPDATE,LINK) SELECT " +
                " %s, %s, %s, %s, %s, %s, %s, %s from dual " +
                "WHERE NOT EXISTS (SELECT 1 FROM BET_HISTORY WHERE title=%s and sport=%s and date=%s)";
        ResultSet resultSet = preparedStatement.executeQuery();
        int counter = 1;
        while (resultSet.next()){
            String title = resultSet.getString(2);
            String sport = resultSet.getString(3);
            String results = resultSet.getString(4);
            String date = resultSet.getString(5);
            String stage = resultSet.getString(6);
            String bettime = resultSet.getString(7);
            String lastupdate = resultSet.getString(8);
            String link = resultSet.getString(9);

            String formattedString = String.format(sqlInsert,
                    wrapWIthQuote(title),
                    wrapWIthQuote(sport),
                    wrapWIthQuote(results),
                    wrapWIthQuote(date),
                    wrapWIthQuote(stage),
                    wrapWIthQuote(bettime),
                    wrapWIthQuote(lastupdate),
                    wrapWIthQuote(link),
                    wrapWIthQuote(title),
                    wrapWIthQuote(sport),
                    wrapWIthQuote(date));
            //System.out.println(formattedString);
            remoteConnection.createStatement().executeUpdate(formattedString);
            System.out.println("Added " + counter + "records");
            counter++;
        }
        connection.close();
        remoteConnection.close();
    }

    public static String wrapWIthQuote(String s){
        if(s==null || "NULL".equals(s) || "null".equals(s)){
            return "null";
        }
        return "'" + s + "'";
    }
}