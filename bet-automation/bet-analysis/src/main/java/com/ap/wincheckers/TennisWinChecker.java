package com.ap.wincheckers;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        result = result.toLowerCase().replaceAll("adv", "45");//

        try {
            int[] score = {Integer.valueOf(result.substring(0, result.indexOf("(")).split("-")[0]),
                    Integer.valueOf(result.substring(0, result.indexOf("(")).split("-")[1])};

            String[] setStrings = result.substring(result.indexOf("(") + 1, result.indexOf(")")).split(",");

            if (setStrings.length < 2) {
                return -1;
            }

            String[] lastSetStrings = setStrings[setStrings.length - 1].split("-");
            int[] lastSetResult = {Integer.valueOf(lastSetStrings[0]), Integer.valueOf(lastSetStrings[1])};

            int advantagesOnTieBreak = -1;
            if(result.contains(":")){
                List<Integer> tieBreakResult = Arrays.stream(result
                        .substring(result.indexOf(")")+1)
                        .trim()
                        .split(":"))
                        .map(e->Integer.valueOf(e))
                        .collect(Collectors.toList());
                if(tieBreakResult.get(0) > tieBreakResult.get(1) && tieBreakResult.get(0) >=5){
                    advantagesOnTieBreak = 1;
                } else if(tieBreakResult.get(0) < tieBreakResult.get(1) && tieBreakResult.get(1) >=5){
                    advantagesOnTieBreak = 2;
                }

                if(advantagesOnTieBreak > 0 && score[advantagesOnTieBreak-1] > 0 ){
                    return advantagesOnTieBreak;
                }

            }

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
        System.out.println(new TennisWinChecker().getWinner("1-1(6-2,2-6,4-5) 15:40"));

    }
}