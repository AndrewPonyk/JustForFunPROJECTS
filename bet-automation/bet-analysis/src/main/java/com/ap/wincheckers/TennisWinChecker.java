package com.ap.wincheckers;


public class TennisWinChecker implements WinChecker {
    /**
     * @param result, example '1-1(6-4,5-7,5-6) 0:30'
     * @return
     */
    @Override
    public int getWinner(String result) {
        if (result == null || !result.contains("(") || !result.contains(")")) {
            return -1;
        }

        try {
            int[] score = {Integer.valueOf(result.substring(0, result.indexOf("(")).split("-")[0]),
                    Integer.valueOf(result.substring(0, result.indexOf("(")).split("-")[1])};

            String[] setStrings = result.substring(result.indexOf("(") + 1, result.indexOf(")")).split(",");

            if (setStrings.length < 2) {
                return -1;
            }

            String[] lastSetStrings = setStrings[setStrings.length - 1].split("-");
            int[] lastSetResult = {Integer.valueOf(lastSetStrings[0]), Integer.valueOf(lastSetStrings[1])};

            if (lastSetResult[0] > lastSetResult[1] && lastSetResult[0] >= 5 && score[0] >= score[1]) {
                return 1;
            } else if (lastSetResult[0] < lastSetResult[1] && lastSetResult[1] >= 5 && score[0] <= score[1]) {
                return 2;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) {
        System.out.println(new TennisWinChecker().getWinner("1-1(6-4,5-7,7-6) 0:30"));

    }
}