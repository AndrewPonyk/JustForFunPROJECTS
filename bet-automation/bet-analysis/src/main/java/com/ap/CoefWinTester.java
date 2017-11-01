package com.ap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CoefWinTester {
    public static class Constants{
        public static Double BET_BASE = 5.0;
        public static Double WIN_COEF_FLAG;
    }
    public static void main(String[] args) {
        Double[] coefs = generateDoubleArray(39);
        simulateWin(coefs, 3);
        System.out.println(Arrays.toString(coefs));
    }

    private static void simulateWin(Double[] coefs, int loseCount) {
        List<Double> winsSumList = new ArrayList<>();
        Constants.WIN_COEF_FLAG = 0.21;

        for(int j = 0;j<coefs.length;j++){
            Double currentSum = 11.0;
            List<Integer> loseIndexes = new ArrayList<>();
            for (int i =0; i < loseCount; i++){
                loseIndexes.add(ThreadLocalRandom.current().nextInt(coefs.length));
            }
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
            System.out.println("Loose indexes are " + StringUtils.join(loseIndexes) +" and Result is: " + currentSum);
            winsSumList.add(currentSum);
        }

        System.out.println("For flag=" + Constants.WIN_COEF_FLAG +" Average win is : " +
                Precision.round(winsSumList.stream().mapToDouble(e->e).average().getAsDouble(),2));
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


    public static Double[] generateDoubleArray(int size){
        Double[] result = new Double[size];
        for (int i = 0; i < size; i++) {
            result[i] = Precision.round(Math.random()/5.8, 2)+1;
            result[i] = 1.11;
            if(result[i] <1.06 ){
                result[i] = Precision.round(Math.random()/6, 2)+1;
                if(result[i] <1.06 ){
                    result[i] = Precision.round(Math.random()/6, 2)+1;
                    if(result[i] <1.05 ){
                        result[i] = Precision.round(Math.random()/6, 2)+1;
                    }
                }
            }
        }

        return result;
    }
}
