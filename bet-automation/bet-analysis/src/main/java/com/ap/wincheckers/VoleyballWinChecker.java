package com.ap.wincheckers;


public class VoleyballWinChecker implements WinChecker {

    /**
     * @param result, example '2-2(25-20,23-25,21-25,25-19,14-11)'
     * @return
     */
    @Override
    public int getWinner(String result) {
        try {
        if (result == null || !result.contains("(") || !result.contains(")")) {
            return -1;
        }

        int[] score = {Integer.valueOf(result.substring(0, result.indexOf("(")).split("-")[0]),
                Integer.valueOf(result.substring(0, result.indexOf("(")).split("-")[1])};

        String[] setStrings = result.substring(result.indexOf("(") + 1, result.indexOf(")")).split(",");

        if (setStrings.length < 3) {
            return -1;
        }

        String[] lastSetStrings = setStrings[setStrings.length - 1].split("-");
        int[] lastSetResult = {Integer.valueOf(lastSetStrings[0]), Integer.valueOf(lastSetStrings[1])};

        int lastSetWinner = lastSetResult[0] > lastSetResult[1] ? 1 : 2;
        if (lastSetResult[0] == lastSetResult[1]) {
            return -1;
        }

        //#TODO: add validation >= 14 if there are 5 sets, >=24 if there are <5 sets

        if ((score[lastSetWinner-1] >= 2)) {
            return lastSetWinner;
        }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) {
        System.out.println(new VoleyballWinChecker().getWinner("2-1(25-20,23-25,21-25,25-19,21-20)"));
    }
}