package com.ap.utils;

import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Constants {
    public static final String LIVE_URL = "https://www.parimatch.com/en/live.html";
    public static final String HISTORY_URL = "https://www.parimatch.com/en/history.html";

    public static final Double baseBetSum = 0.017;

    public static final double FIRST_STAGE_COEF = 1.26;
    public static final double SECOND_STAGE_COEF = 1.4;
    public static final double THIRD_STAGE_COEF = 1.21;

    public static final double FOURTH_STAGE_COEF = 1.45;
    public static final double FIFTH_STAGE_COEF = 1.17;

    //public static final double SIXTH_STAGE_COEF = 1.3;
    //public static final double SEVENTH_STAGE_COEF = 1.15;

    public static final String ERROR_STATUS = "ERROR";

    public static final Double BET_BASE = 5.0;
    public static final Double MIN_BET = 3.0;
    public static final Double WIN_COEF_FLAG = 0.4; // max bet equals BET_BASE+BET_BASE*WIN_COEF_FLAG

    public static final Double COMEBACK_LIMIT = 3.5;
    public static final Double[] COMEBACK_PERFORM_BET_BOUND = {2.25, 2.6};
    public static final String PLAYER1_CROLL_LIMIT = "PLAYER1_CROSS_LIMIT";
    public static final String PLAYER2_CROLL_LIMIT = "PLAYER2_CROSS_LIMIT";
    public static final String RETURNS_TO_BET_BOUND = "RETURS_TO_BET_BOUND";

    public static final Set<String> SPORTS = new HashSet<>(Arrays.asList(
    "Tennis",
    "Basketball",
    "Volleyball",
    "Hockey",
    "Badminton"));

    public static final Double BET_LIMIT = 1.26;

    public static String BET_EMAIL = "";
    public static String BET_PASSWORD = "";

    public static String PAR_EMAIL = "";
    public static String PAR_PASS = "";

    public static String STATUS_URL = "https://raw.githubusercontent.com/AndrewPonyk/JustForFunPROJECTS/master/bet-automation/remote-status.txt";

    static {
        try {
            List<String> strings = FileUtils.readLines(new File("/home/andrii/pass.txt"), "UTF-8");
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

}