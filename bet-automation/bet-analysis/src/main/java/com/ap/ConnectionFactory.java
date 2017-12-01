package com.ap;

import com.mysql.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static Connection connection = null;
    //    public static final String URL = "jdbc:mysql://104.196.174.243:3306/parimatch";
    public static final String URL = "jdbc:mysql://localhost:3306/parimatch";
    public static final String USER = "root";
    public static final String PASS = "root";

    public static Connection getConnection()
    {
        if(connection != null){
            return connection;
        }
        try {
            DriverManager.registerDriver(new Driver());
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException ex) {
            throw new RuntimeException("Error connecting to the database", ex);
        }
    }
}
