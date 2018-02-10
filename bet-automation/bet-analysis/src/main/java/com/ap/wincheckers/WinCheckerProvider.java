package com.ap.wincheckers;


import java.util.HashMap;
import java.util.Map;

public class WinCheckerProvider {
    private static Map<String, WinChecker> CHECKERS = new HashMap<>();
    static {
        CHECKERS.put("basketball", new BasketballWinChecker());
        CHECKERS.put("volleyball", new VoleyballWinChecker());
        CHECKERS.put("tennis", new TennisWinChecker());
    }

    public static WinChecker getWinChecker(String sport){
        return CHECKERS.get(sport.toLowerCase());
    }
}