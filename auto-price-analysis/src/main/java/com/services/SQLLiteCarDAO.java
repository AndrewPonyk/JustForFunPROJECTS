package com.services;

import com.dto.Advert;
import org.sqlite.SQLiteConnection;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by andrii on 28.01.16.
 */
public class SQLLiteCarDAO implements CarDAO {

    Connection connection;
    Statement stmt;

    @Override
    public Integer saveCars(List<Advert> cars) {
        int addedCars = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + ProjectConfig.carsDBFile);
            stmt = connection.createStatement();
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            for(Advert car : cars){
                String sql = String.format("insert into CARS(HASH, CREATE_DATE, MODEL, NAME, CITY, PRICE, YEAR," +
                                "MILEAGE, AUTORIA_ID, DESCRIPTION, AVG_PRICE) values " +
                                "('%s','%s','%s','%s','%s',%s,%s,%s,'%s','%s',%s)",
                        car.getHash(), today, car.model, car.name, car.city, car.price, car.year, car.mileage, car.autoriaId,
                        car.description, car.averagePriceOnThisDate
                        );
                stmt.executeUpdate(sql);
            }

            connection.close();
            return 0;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }


}

/*
* SELECT * FROM CARS;

CREATE TABLE CARS (id INTEGER  PRIMARY KEY AUTOINCREMENT,
  HASH VARCHAR (10),
  CREATE_DATE DATE,
  MODEL VARCHAR(50),
  NAME VARCHAR(50),
  CITY VARCHAR (50),
  PRICE INTEGER,
  YEAR INTEGER,
  MILEAGE INTEGER,
  AUTORIA_ID VARCHAR (20),
  DESCRIPTION VARCHAR (200),
  AVG_PRICE INTEGER,
  UNIQUE(HASH, CREATE_DATE) ON CONFLICT REPLACE
)
* */
