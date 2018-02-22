package com.ap.monitor;

import com.ap.dao.BetRepo;
import com.ap.dao.BetRepoJdbc;
import com.ap.utils.BetDomUtils;
import com.ap.utils.Constants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class CurrentBetStatusMonitor implements Runnable {

    private static final WebDriverWait wait;
    public static ChromeDriver driver;
    public static Integer LAST_BET_STATUS = -10;

    static {
        System.setProperty("webdriver.chrome.driver", Constants.driver());
        System.setProperty("webdriver.gecko.driver", "/home/andrii/Programs/geckodriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 9);
    }

    BetRepo betRepo = new BetRepoJdbc();

    @Override
    public void run() {
        while (true){
            System.err.println("Monitor current bet status");
            try {
                login();
                parseAndUpdateLastBetAndCurrentAmount();
                Thread.sleep(95000);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void parseAndUpdateLastBetAndCurrentAmount() {
        try {
            List<WebElement> pastBetsSums = driver.findElementsByCssSelector(".lr .so");
            WebElement lastBet = pastBetsSums.get(0);
            String winOfLastBet = lastBet.getText();

            List<WebElement> lastBets = driver.findElementsByCssSelector(".lr");
            String lastBetTitle = lastBets.get(0).findElement(By.cssSelector(".row-info .col-2")).getText();

            Integer lastBetStatus = 0;
            Double currentAmount = Double.valueOf(driver.findElement(By.cssSelector(".saldo"))
                    .getText()
                    .replaceAll("[^\\d.]", ""));

            if (winOfLastBet.startsWith("0.0")) {
                //lose last bet
                lastBetStatus = -1;
            }

            if (!winOfLastBet.isEmpty() && !winOfLastBet.startsWith("0.0")) {
                //win last bet
                lastBetStatus = 1;
            }

            if (winOfLastBet.isEmpty()) {
                // last bet in progress
                lastBetStatus = 0;
            }

            LAST_BET_STATUS = lastBetStatus;
            betRepo.updateCurrentBetStatus(lastBetStatus, currentAmount, getLastLoseBetsSum(), lastBetTitle);

        } catch (Exception e) {
            System.err.println("Error happens during update CURRENT_BET_STATUS {}" + e);
        }
        System.out.println("CURRENT_BET_STATUS updated successfully");
    }

    private Double getLastLoseBetsSum() {
        Double result = 0.0;
        try {
            List<WebElement> pastBets = driver.findElementsByCssSelector(".lr");
            for (WebElement bet : pastBets) {
                String sumOfLastBet = bet.findElement(By.cssSelector(".so")).getText();
                if (sumOfLastBet.startsWith("0.0")) {
                    //lose this bet
                    String loseBetSum = bet.findElements(By.cssSelector(".lrBold")).get(1).getText();
                    Double betSum = Double.valueOf(loseBetSum.substring(0, loseBetSum.indexOf(" ")));
                    result += betSum;
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    private void syncResultsWithResultsPage(){
        // perform results sync, check last results with bet_history table
        //https://www.parimatch.com/en/res.html?&Date=20180222&SK=0

    }

    //specific behavior
    private void login() {
        try {
            driver.get(Constants.HISTORY_URL);
            WebElement loginInput = (new WebDriverWait(driver, 5)).
                    until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='username']")));
            WebElement passwordInput = driver.findElement(By.cssSelector("input[name='passwd']"));
            WebElement okButton = driver.findElement(By.cssSelector(".btn_orange.ok"));
            //okButton.click();

            //loginInput = (new WebDriverWait(driver, 5)).
            //        until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='username']")));
            //passwordInput = driver.findElement(By.cssSelector("input[name='passwd']"));
            //okButton = driver.findElement(By.cssSelector(".btn_orange.ok"));
            BetDomUtils.setAttribute(driver, loginInput, "value", Constants.PAR_EMAIL);
            BetDomUtils.setAttribute(driver, passwordInput, "value", Constants.PAR_PASS);
            okButton.click();
        } catch (Exception e) {
            System.out.println("already logged on history page");
        }
    }
}