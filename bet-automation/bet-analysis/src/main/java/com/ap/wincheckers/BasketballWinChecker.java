package com.ap.wincheckers;


public class BasketballWinChecker implements WinChecker {

    /**
     *
     * @param result, example '84-89(14-20,18-14,24-27,19-14,9-14)'
     * @return
     */
    @Override
    public int getWinner(String result) {
        if(result == null || result.trim().length() == 0){
            return -1;
        }
        try {
            int[] score;
            if(result.contains("(")){
                score = new int[]{Integer.valueOf(result.substring(0, result.indexOf("(")).split("-")[0]),
                        Integer.valueOf(result.substring(0, result.indexOf("(")).split("-")[1])};

                String[] quarterStrings = result.substring(result.indexOf("(") + 1, result.indexOf(")")).split(",");
                if(quarterStrings.length < 4 && Math.max(score[0], score[1]) < 60){
                    return -1;
                }
            } else {
                score = new int[]{Integer.valueOf(result.split("-")[0]),
                        Integer.valueOf(result.split("-")[1])};
            }



            if(score[0] > score[1]){
                return 1;
            } else if(score[0] < score[1]){
                return 2;
            }

            if(score[0] == score[1]){
                return 3; // no winner
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) {
        System.out.println(new BasketballWinChecker().getWinner("888-89(14-20,18-14,24-27,19-14,9-14)"));
    }
}