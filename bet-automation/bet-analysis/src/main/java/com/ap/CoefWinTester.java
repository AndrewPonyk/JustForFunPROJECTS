package com.ap;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoefWinTester {
    public static class Constants{
        public static Double BET_BASE = 5.0;
        public static Double WIN_COEF_FLAG;
    }
    public static void main(String[] args) {
        Double[] coefs = new Double[]{1.12, 1.12, 1.12, 1.12, 1.12, 1.12, 1.12, 1.12, 1.11, 1.11, 1.11, 1.11, 1.11};
        List<Double> winsSumList = new ArrayList<>();
        Constants.WIN_COEF_FLAG = 0.3;

        //while (Constants.WIN_COEF_FLAG <2){

            for(int j = 0;j<coefs.length;j++){
                Double currentSum = 11.0;
                List<Integer> loseIndexes = Arrays.asList(j);
                for (int i =0;i<coefs.length;i++) {
                    Double betSum = getBetSum(currentSum);
                    Double winSum = coefs[i] * betSum - betSum;
                    //debug System.out.println(currentSum+"<-current: "+ i +" : betsum->" + betSum);
                    if(!loseIndexes.contains(i)){
                        currentSum = currentSum + winSum;
                    } else {
                        currentSum = currentSum - betSum;
                    }

                }
                System.out.println("Loose index " + j +" and Result is: " + currentSum);
                winsSumList.add(currentSum);
            }

            System.out.println("For flag=" + Constants.WIN_COEF_FLAG +" Average win is : " +
                    Precision.round(winsSumList.stream().mapToDouble(e->e).average().getAsDouble(),2));
            //Constants.WIN_COEF_FLAG = Constants.WIN_COEF_FLAG + 0.05;
        //}

    }

    public static Double getBetSum(Double currBalance){

        //old way
        //Double betSum = Constants.BET_BASE + currBalance%Constants.BET_BASE;
        Double betSum = 0d;
        if(currBalance % Constants.BET_BASE < (Constants.BET_BASE*Constants.WIN_COEF_FLAG)){
            betSum = Constants.BET_BASE + currBalance % Constants.BET_BASE;
        }else {
            betSum = currBalance % (Constants.BET_BASE) % (Constants.BET_BASE*Constants.WIN_COEF_FLAG) + Constants.BET_BASE;
        }
        if(betSum > currBalance){
            betSum = currBalance;
        }

        return betSum;
    }
}
