package com.ap.services;

import com.ap.model.BetCoef;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Scanner;

/**
 * Created by andrii on 24.06.17.
 */
public class WebRetriever {
    static {
        System.setProperty("webdriver.gecko.driver", "/home/andrii/Programs/geckodriver");
        driver = new FirefoxDriver();
    }

    public static FirefoxDriver driver;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {

            String playerName = scanner.nextLine();
            if (playerName == null || "".equals(playerName)) {
                break;
            }
            BetCoef result = getBetCoef(playerName);

            System.out.println(result.toString());
        }
    }

    public static BetCoef getBetCoef(String playerName) {
        BetCoef result = new BetCoef();
        int playerPosition = 0; // 0 or 1
        try {


            String[] nameParts = playerName.split(" ");
            driver.get("http://www.sofascore.com/ru/search?q=" + playerName);

            List<WebElement> infoLinks = driver.findElements(By.cssSelector(".js-page-search-teams .cell__section--main .cell__content a"));
            WebElement player = infoLinks.get(infoLinks.size() - 1);
            player.click();

            WebDriverWait wait = new WebDriverWait(driver, 19);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".h1.event-live")));

            WebElement match = driver.findElement(By.cssSelector(".h-interactive.js-event-link"));
            String[] matchPlayers = match.getText().split(" - ");
            matchPlayers[0] = StringUtils.stripAccents(matchPlayers[0]);

            List<WebElement> elements = driver.findElements(By.cssSelector(".vote__stats"));
            System.out.println(elements.get(0).getText());
            String[] coefParts = elements.get(0).getText().split("\n");

            System.out.println("Detect position:" + matchPlayers[0]);
            System.out.println(nameParts[0] +":" + nameParts[1]);
            System.out.println("------");
            playerPosition = matchPlayers[0].contains(nameParts[0]) || matchPlayers[0].contains(nameParts[1]) ? 0 : 1;
            if (playerPosition == 0) {
                result.setVotersCount(Integer.valueOf(coefParts[0]));
                if(coefParts[1].contains("%")){
                    result.setCoef(Double.valueOf(coefParts[1].replace("%", "")));
                }
            } else {
                if(coefParts.length == 4){
                    result.setVotersCount(Integer.valueOf(coefParts[2]));
                    result.setCoef(Double.valueOf(coefParts[3].replace("%", "")));
                }

                if(coefParts.length == 3){
                    result.setVotersCount(Integer.valueOf(coefParts[1]));
                    result.setCoef(Double.valueOf(coefParts[2].replace("%", "")));
                }
            }

        } catch (Exception e) {
            result = new BetCoef(0, 0.0); // no data or error
        }

        try {
            // check 365 bets
            List<WebElement> links365 = driver.findElements(By.cssSelector(".js-event-page-odds-container .odds__table tr"));
            if(!links365.isEmpty()){
                WebElement row365 = links365.get(1);
                List<WebElement> coefs365 = row365.findElements(By.cssSelector("td"));
                if(coefs365.size() == 2){
                    result.setBet365Coef(Double.valueOf(coefs365.get(playerPosition).getText()));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }
}