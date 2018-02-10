package com.ap.utils;


import com.ap.model.MomentResult;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;

public class ValidationUtils {
    public static Boolean validateSport(String text) {
        if(Constants.SPORTS.contains(text)){
            return true;
        }

        return false;
    }

    public static Boolean validateCompetitions(String competitions){
        for (String stopWord : Constants.STOP_WORDS) {
            if(competitions.toLowerCase().contains(stopWord)){
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        Double d = 3.456;
        System.out.println(checkCoefAndNoBetInProgress(null));

    }

    //Outcome "Morocco - Angola:Win 2" - the odds have been changed to "1.80".
    public static Boolean checkCoefAndNoBetInProgress(FirefoxDriver driver) {
        try {
            if (driver.getPageSource().contains("have been changed")) {
                String changeCoefText = driver.findElement(By.cssSelector("#stakeHolder ol li")).getText();
                //String changeCoefText = "Outcome \"Morocco - Angola:Win 2\" - the odds have been changed to \"1.18\".";
                changeCoefText = changeCoefText.substring(changeCoefText.indexOf("to \"") + 4);
                changeCoefText = changeCoefText.replaceAll("\".$", "");
                Double changedCoef = Double.valueOf(changeCoefText);
                System.err.println("Coef in new MESSAGE " + changedCoef);
                if (changedCoef > Constants.FIFTH_STAGE_COEF) {
                    return false;
                } else {
                    return true;
                }
            }

            WebElement coefElement = driver.findElement(By.cssSelector("#wb .td_cf"));
            Double coef = Double.valueOf(coefElement.getText());
            System.err.println("COEF in TABLE " + coef);

            if (coef > Constants.FIFTH_STAGE_COEF) {
                return false;
            }

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        return false;
    }

    public static Double getCurrentCoef(FirefoxDriver driver){
        try {
            if (driver.getPageSource().contains("have been changed")) {
                String changeCoefText = driver.findElement(By.cssSelector("#stakeHolder ol li")).getText();
                //String changeCoefText = "Outcome \"Morocco - Angola:Win 2\" - the odds have been changed to \"1.18\".";
                changeCoefText = changeCoefText.substring(changeCoefText.indexOf("to \"") + 4);
                changeCoefText = changeCoefText.replaceAll("\".$", "");
                Double changedCoef = Double.valueOf(changeCoefText);
                System.err.println("Coef in new MESSAGE " + changedCoef);
                return changedCoef;
            }

            WebElement coefElement = driver.findElement(By.cssSelector("#wb .td_cf"));
            Double coef = Double.valueOf(coefElement.getText());
            System.err.println("COEF in TABLE " + coef);

            return coef;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return -1.0;
    }

    public boolean checkFirst4Results(List<MomentResult> results){
        return false;
    }

}
