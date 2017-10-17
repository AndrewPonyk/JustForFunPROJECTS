package com.ap.utils;

public class Constants {
    public static final String LIVE_URL = "https://www.parimatch.com/en/live.html";

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
    public static final Double WIN_COEF_FLAG = 0.45; // max bet equals BET_BASE+BET_BASE*WIN_COEF_FLAG
}
