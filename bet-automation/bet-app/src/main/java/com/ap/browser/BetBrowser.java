package com.ap.browser;

import com.ap.dao.BetRepo;
import com.ap.dao.BetRepoJdbc;
import com.ap.model.BetItem;
import com.ap.utils.BetDomUtils;
import com.ap.utils.Constants;
import com.ap.utils.RegexUtils;
import com.ap.utils.ValidationUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class BetBrowser {
    static {
        System.setProperty("webdriver.gecko.driver", "/home/andrii/Programs/geckodriver");
        driver = new FirefoxDriver();
    }

    public static FirefoxDriver driver;
    public static BetRepo betRepo = new BetRepoJdbc();

    public void start() {
        login();
        goToLivePage();
        startMonitoring();
    }

    private void startMonitoring() {
        BetItem bet = null;
        // one time in 25 seconds
        while (true) {
            parseAndSaveBets();
            bet = checkAndBet();

            if (bet == null || bet.getStage().contains("ERROR")) {
                //try again, but only 1 time
                bet = checkAndBet();
            }

            if (bet != null) {
                // update in db
                betRepo.markBetAsCompleted(bet);
            }

            try {
                Thread.sleep(13000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void parseAndSaveBets() {
        try {
            RegexUtils.currentTime = LocalDateTime.now();
            String betTable = driver.findElements(By.cssSelector("#inplay")).get(0).getAttribute("outerHTML");
            System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            Document betTableElement = Jsoup.parse(betTable);
            Elements betRows = betTableElement.select("#inplay table.dt");
            System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

            LinkedList<BetItem> betItems = new LinkedList<>();
            final AtomicInteger countSkip = new AtomicInteger(0);
            betRows.forEach(row -> {
                try {
                    Element win1Element = row.select("tr td:nth-child(3)").first();
                    Element win2Element = row.select("tr td:nth-child(5)").first();
                    String sport = row.parent().parent().parent().select("p.sport").first().text();
                    if (ValidationUtils.validateSport(sport)) {
                        String betText = row.html();
                        if(!betText.toLowerCase().contains("corners")){
                            betItems.add(RegexUtils.parseBetItem(sport, betText,
                                    win1Element.text(), win2Element.text()));
                        }
                    }

                } catch (Exception wrondInvisibleRow) {
                    //this slow down app, so comment
                    //System.err.println(wrondInvisibleRow.getMessage());
                    countSkip.incrementAndGet();
                }
            });
            System.out.println("LENGHT = " + betRows.size());
            System.err.println("Skipped items:: " + countSkip.get());
            final StringBuilder betItemsString = new StringBuilder();
            betItems.forEach(e -> {
                betItemsString.append(e.toString());
                betItemsString.append("=====================\n");
            });
            System.out.println(betItemsString);
            betRepo.saveUpdateItems(betItems);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private BetItem checkAndBet() {
        BetItem stage3Item = null;
        List<BetItem> stage3Items = betRepo.getStage5Items();
        System.out.println("Try to find sssssssssssssssssssssssssssstage 333333");
        if (!stage3Items.isEmpty()) {
            String parentWindowHandler="";
            stage3Item = stage3Items.get(0);
            System.out.println("FOUND STAGE 3" + stage3Item.getTitle());
            goToLivePage();
            login();
            Integer playerInStage3 = 0;
            if (stage3Item.getStage().startsWith("player1:5")) {
                playerInStage3 = 1;
            } else if (stage3Item.getStage().startsWith("player2:5")) {
                playerInStage3 = 2;
            }

            try {
                //List<WebElement> betRows = driver.findElements(By.cssSelector("#inplay table.dt"));
                List<WebElement> betRows = driver.findElements(By.xpath("//div[@id='inplay']//table[contains(@class,'dt') and contains(normalize-space(),'" +
                        stage3Item.getTitle().substring(0,5) +
                        "')]"));

                System.out.println(":::" + betRows.size());
                if(betRows.isEmpty()){
                    throw new Exception("Bet is not present anymore");
                }

                for (WebElement row : betRows) {
                    String rowText = row.getText().replace("<small>", "").replace("</small>", "").replaceAll("'", "");
                    if (rowText.contains(stage3Item.getTitle())) {

                        WebElement winElement = playerInStage3 == 1 ? row.findElement(By.cssSelector("tr td:nth-child(3)")) :
                                row.findElement(By.cssSelector("tr td:nth-child(5)"));
                        winElement.click();
                        Thread.sleep(100);
                        driver.findElement(By.cssSelector(".btn_orange")).click();

                        parentWindowHandler = driver.getWindowHandle();
                        String subWindowHandler = "";
//
                        Set<String> handles = driver.getWindowHandles();
                        Iterator<String> iterator = handles.iterator();
                        while (iterator.hasNext()) {
                            String next = iterator.next();
                            if (!next.equals(parentWindowHandler)) {
                                subWindowHandler = next;
                            }
                        }


                        driver.switchTo().window(subWindowHandler); // switch to popup window
                        System.out.println("SUBWINDOW:" + driver.getCurrentUrl());
                        Thread.sleep(500);
                        System.out.println("-----------------------------------------------------------");
                        WebElement sumElement = driver.findElement(By.cssSelector("input[name=sums]"));
                        WebElement currBalanceElement = driver.findElement(By.cssSelector("#ownerInfo td b"));
                        Double currBalance = Double.parseDouble(currBalanceElement.getText().replaceAll("UAH", "").trim());

                        if( currBalance < Constants.MIN_BET ){
                            stage3Item.setStage(stage3Item.getStage() + Constants.ERROR_STATUS + "No_enough_money");
                            return stage3Item;
                        }
                        //old way
                        //Double betSum = Constants.BET_BASE + currBalance%Constants.BET_BASE;
                        Double betSum = 0d;
                        if(currBalance % Constants.BET_BASE < (Constants.BET_BASE*Constants.WIN_COEF_FLAG)){
                            betSum = Constants.BET_BASE + currBalance % Constants.BET_BASE;
                        }else {
                            betSum = currBalance % (Constants.BET_BASE*Constants.WIN_COEF_FLAG) + Constants.BET_BASE;
                        }
                        if(betSum > currBalance){
                            betSum = currBalance;
                        }

                        BetDomUtils.setAttribute(driver, sumElement, "value", "" + betSum);
                        if (driver.getPageSource().contains("Errors list") &&
                                !driver.getPageSource().contains("have been changed")) {
                            stage3Item.setStage(stage3Item.getStage() + "ERROR");
                        }else {
                            Thread.sleep(100);
                            if(ValidationUtils.checkCoef(driver)){
                                driver.findElement(By.cssSelector(".btn_orange")).click();
                                // looks like click waits until bet is done, but for sure I added this sleep
                                Thread.sleep(3000);
                            } else {
                                // if coef is 5, and while betting it has changed (to 1.3 for example, we set stage:4 back)
                                //stage3Item.setStage(stage3Item.getStage() + Constants.ERROR_STATUS + "COEFFF");
                                stage3Item.setStage(stage3Item.getStage().replace("5","4"));
                            }


                            if (driver.getPageSource().contains("Errors list")) {
                                stage3Item.setStage(stage3Item.getStage() + Constants.ERROR_STATUS);
                            } else {
                                stage3Item.setStage(stage3Item.getStage() + "COMPLETED");
                            }
                        }
                        return stage3Item;
                    }
                    stage3Item.setStage(stage3Item.getStage()+"ERROR203Line");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                stage3Item.setStage(stage3Item.getStage() + Constants.ERROR_STATUS);
                return stage3Item;
            }
            finally {
                if( parentWindowHandler != null && !parentWindowHandler.isEmpty()){
                    System.err.println("Close window:::" + driver.getCurrentUrl());
                    driver.close();
                    driver.switchTo().window(parentWindowHandler);
                }
            }
        }
        return stage3Item;
    }

    private void goToLivePage() {
        try {
            //if(!driver.getCurrentUrl().contains("live.html")){
                driver.get("https://www.parimatch.com/en/live.html");
            //}
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private void login() {
        try {
            driver.get(Constants.LIVE_URL);
            WebElement loginButton = driver.findElement(By.className("login"));
            loginButton.click();
        } catch (Exception e) {
            System.out.println("Click login error or we are already logged in");
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
            System.out.println("Error with find login inputs");
            e.printStackTrace();
        }
    }
}