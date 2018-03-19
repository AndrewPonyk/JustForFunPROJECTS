package com.ap.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Constants {
    public static final String LIVE_URL = "https://www.parimatch.com/en/live.html";
    public static final String HISTORY_URL = "https://www.parimatch.com/en/history.html";

    public static final double FIRST_STAGE_COEF = 1.26;
    public static final double SECOND_STAGE_COEF = 1.5;
    public static final double THIRD_STAGE_COEF = 1.22;

    public static final double FOURTH_STAGE_COEF = 1.45;
    public static final double FIFTH_STAGE_COEF = 1.17;

    //public static final double SIXTH_STAGE_COEF = 1.3;
    //public static final double SEVENTH_STAGE_COEF = 1.15;

    public static final String ERROR_STATUS = "ERROR";
    public static final String SKIPPED_STATUS = "SKIPPED";
    public static final String COMPLETED = ":Completed";

    public static final Double BET_BASE = 5.0;
    public static final Double MIN_BET = 3.0;
    public static final Double WIN_COEF_FLAG = 0.4; // max bet equals BET_BASE+BET_BASE*WIN_COEF_FLAG

    public static final Double BET_LIMIT = 1.26;
    public static final Double WEEKEND_BET_LIMIT = 1.2; //on saturday and sunday need to decrease bets count


    public static final Double FIRST_BET_IN_PERCENTS = 0.013;
    public static final Double PROFIT_RATIO = 1.06;
//
//    public static final Double COMEBACK_LIMIT = 3.3;
//    public static final Double[] COMEBACK_PERFORM_BET_BOUND = {2.25, 2.55};
//    public static final String PLAYER1_CROLL_LIMIT = "PLAYER1_CROSS_LIMIT";
//    public static final String PLAYER2_CROLL_LIMIT = "PLAYER2_CROSS_LIMIT";
//    public static final String RETURNS_TO_BET_BOUND = "RETURS_TO_BET_BOUND";
//    public static final List<String> STOP_WORDS = Arrays.asList(
//            "poland", "Basketball china", "Badminton");

    //try this, without basket
    public static final Double COMEBACK_LIMIT = 1.95;
    public static final Double[] COMEBACK_PERFORM_BET_BOUND = {1.54, 1.86};
    public static final String PLAYER1_CROLL_LIMIT = "ZPLAYER1_CROSS_LIMIT";
    public static final String PLAYER2_CROLL_LIMIT = "ZPLAYER2_CROSS_LIMIT";
    public static final String RETURNS_TO_BET_BOUND = "ZRETURS_TO_BET_BOUND";
    public static final List<String> STOP_WORDS = Arrays.asList(
            "poland", "Basketball", "Badminton","volleyball", "regional greece", "volleyball Qatar", "saudi", "iran", "/", "doubles",
            "volleyball japan", "volleyball korea", "univer",
            "kolkata", "bhopal","mumbai",
            "algeria", "afric", "egypt", "angola",
            "belarus", "qualifying");

    public static final Set<String> SPORTS = new HashSet<>(Arrays.asList(
    "Tennis",
    "Basketball",
    "Volleyball"));

    public static String BET_EMAIL = "";
    public static String BET_PASSWORD = "";

    public static String PAR_EMAIL = "";
    public static String PAR_PASS = "";

    public static String STATUS_URL = "https://raw.githubusercontent.com/AndrewPonyk/JustForFunPROJECTS/master/bet-automation/remote-status.txt";

    static {
        try {
            File passFile = new File("/home/andrii/pass.txt");
            if(!passFile.exists()){
                passFile = new File("C:\\tmp\\pass.txt");
            }

            List<String> strings = FileUtils.readLines(passFile, "UTF-8");
            System.out.println(strings);
            BET_EMAIL = strings.get(0);
            BET_PASSWORD = strings.get(1);
            PAR_EMAIL = strings.get(2);
            PAR_PASS = strings.get(3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("test constants");
    }

    public static String driver(){
        File driver = new File("C:\\tmp\\chromedriver.exe");
        if(driver.exists()){
            return "C:\\tmp\\chromedriver.exe";
        } else {
            return "/home/andrii/Programs/chromedriver";
        }
    }
}