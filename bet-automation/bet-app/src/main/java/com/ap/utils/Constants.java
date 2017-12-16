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

    public static final double FIRST_STAGE_COEF = 1.2;
    public static final double SECOND_STAGE_COEF = 1.4;
    public static final double THIRD_STAGE_COEF = 1.18;

    public static final double FOURTH_STAGE_COEF = 1.35;
    public static final double FIFTH_STAGE_COEF = 1.17;

    //public static final double SIXTH_STAGE_COEF = 1.3;
    //public static final double SEVENTH_STAGE_COEF = 1.15;

    public static final String ERROR_STATUS = "ERROR";

    public static final Double BET_BASE = 5.0;
    public static final Double MIN_BET = 3.0;
    public static final Double WIN_COEF_FLAG = 0.4; // max bet equals BET_BASE+BET_BASE*WIN_COEF_FLAG

    public static final Set<String> SPORTS = new HashSet<>(Arrays.asList(
    "Tennis",
    "Basketball",
    "Volleyball",
    "Handball",
    "Hockey",
    "Badminton"));

    public static final Double BET_LIMIT = 1.25;

    public static String BET_EMAIL = "";
    public static String BET_PASSWORD = "";

    static {
        try {
            List<String> strings = FileUtils.readLines(new File("/home/andrii/pass.txt"), "UTF-8");
            System.out.println(strings);
            BET_EMAIL = strings.get(0);
            BET_PASSWORD = strings.get(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("test constants");
    }

}
