package com.ap.monitor;

import com.ap.dao.BetRepo;
import com.ap.dao.BetRepoJdbc;
import com.ap.utils.BetDomUtils;
import com.ap.utils.Constants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class CurrentBetStatusMonitor implements Runnable {

    static {
        System.setProperty("webdriver.gecko.driver", "/home/andrii/Programs/geckodriver");
        driver = new FirefoxDriver();
    }

    public static FirefoxDriver driver;

    BetRepo betRepo = new BetRepoJdbc();

    @Override
    public void run() {
        while (true){
            System.err.println("Monitor current bet status");
            try {
                login();
                goToHistoryPage();
                parseAndUpdateLastBetAndCurrentAmount();
                Thread.sleep(70000);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void parseAndUpdateLastBetAndCurrentAmount() {
        try {
        List<WebElement> pastBets = driver.findElementsByCssSelector(".lr .so");
        WebElement lastBet = pastBets.get(0);
        String winOfLastBet = lastBet.getText();

        Integer lastBetStatus = 0;
        Double currentAmount= Double.valueOf(driver.findElement(By.cssSelector(".saldo"))
                .getText()
                .replaceAll("[^\\d.]", ""));

        if(winOfLastBet.startsWith("0.0")){
            //lose last bet
            lastBetStatus = -1;
        }

        if(!winOfLastBet.isEmpty() && !winOfLastBet.startsWith("0.0")){
            //win last bet
            lastBetStatus = 1;
        }

        if(winOfLastBet.isEmpty()){
            // last bet in progress
            lastBetStatus = 0;
        }

        betRepo.updateCurrentBetStatus(lastBetStatus, currentAmount, getLastLoseBetsSum());

        }catch (Exception e){
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

    private void goToHistoryPage() {
        try {
            driver.get(Constants.HISTORY_URL);
        } catch (Exception e) {
            //logger.info(e.getMessage());
        }

    }

    private void login() {
        try {
            driver.get(Constants.HISTORY_URL);
            WebElement loginButton = driver.findElement(By.className("login"));
            loginButton.click();
        } catch (Exception e) {
            //logger.info("Click login error or we are already logged in");
            // we are logged
            return;
        }

        try {
            WebElement loginInput = (new WebDriverWait(driver, 10)).
                    until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='username']")));
            WebElement passwordInput = driver.findElement(By.cssSelector("input[name='passwd']"));
            WebElement okButton = driver.findElement(By.cssSelector(".btn_orange.ok"));

            BetDomUtils.setAttribute(driver, loginInput, "value", "andrew9999@ukr.net");
            BetDomUtils.setAttribute(driver, passwordInput, "value", "Aa123456");
            okButton.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}