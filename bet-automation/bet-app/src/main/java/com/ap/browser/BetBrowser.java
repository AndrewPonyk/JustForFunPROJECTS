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
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
                Thread.sleep(21000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void parseAndSaveBets() {
        try {
            String betTable = driver.findElements(By.cssSelector("#inplay")).get(0).getAttribute("outerHTML");
            System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            System.out.println(betTable);
            Document betTableElement = Jsoup.parse(betTable);
            betTableElement.select("#inplay table.dt");
            System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

            List<WebElement> betRows = driver.findElements(By.cssSelector("#inplay table.dt"));
            LinkedList<BetItem> betItems = new LinkedList<>();

            final AtomicInteger countSkip = new AtomicInteger(0);
            betRows.forEach(row -> {
                try {
                    //WebElement parent = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    //        "return arguments[0].parentNode.parentNode.parentNode;", row);
                    //WebElement sport = parent.findElement(By.cssSelector("p.sport"));
                    WebElement win1Element = row.findElement(By.cssSelector("tr td:nth-child(3)"));
                    WebElement win2Element = row.findElement(By.cssSelector("tr td:nth-child(5)"));

                    //if (ValidationUtils.validateSport(sport.getText())) {
                        String betText = row.getAttribute("innerHTML");
                        if(!betText.toLowerCase().contains("corners")){
                            betItems.add(RegexUtils.parseBetItem("nosport"/*sport.getText()*/, betText,
                                    win1Element.getText(), win2Element.getText()));
                        }
                    //}


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
        List<BetItem> stage3Items = betRepo.getStage5Items();
        System.out.println("Try to find sssssssssssssssssssssssssssstage 333333");
        if (!stage3Items.isEmpty()) {
            String parentWindowHandler="";
            BetItem stage3Item = stage3Items.get(0);
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
                List<WebElement> betRows = driver.findElements(By.cssSelector("#inplay table.dt"));
                System.out.println(":::" + betRows.size());
                for (WebElement row : betRows) {
                    String rowText = row.getText().replace("<small>", "").replace("</small>", "");
                    if (rowText.contains(stage3Item.getTitle())) {

                        WebElement winElement = playerInStage3 == 1 ? row.findElement(By.cssSelector("tr td:nth-child(3)")) :
                                row.findElement(By.cssSelector("tr td:nth-child(5)"));
                        winElement.click();
                        Thread.sleep(500);
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
                        Thread.sleep(1000);
                        System.out.println("-----------------------------------------------------------");
                        WebElement sumElement = driver.findElement(By.cssSelector("input[name=sums]"));
                        WebElement currBalanceElement = driver.findElement(By.cssSelector("#ownerInfo td b"));
                        Double currBalance = Double.parseDouble(currBalanceElement.getText().replaceAll("UAH", "").trim());

                        if( currBalance < Constants.MIN_BET ){
                            stage3Item.setStage(stage3Item.getStage() + Constants.ERROR_STATUS + "No_enough_money");
                        }
                        Double betSum = Constants.BET_BASE + currBalance%Constants.BET_BASE;

                        BetDomUtils.setAttribute(driver, sumElement, "value", "" + betSum);
                        if (driver.getPageSource().contains("Errors list") &&
                                !driver.getPageSource().contains("have been changed")) {
                            stage3Item.setStage(stage3Item.getStage() + "ERROR");
                        }else {
                            Thread.sleep(200);
                            if(ValidationUtils.checkCoef(driver)){
                                driver.findElement(By.cssSelector(".btn_orange")).click();
                                // looks like click waits until bet is done, but for sure I added this sleep
                                Thread.sleep(3000);
                            } else {
                                stage3Item.setStage(stage3Item.getStage() + Constants.ERROR_STATUS + "COEFFF");
                            }


                            if (driver.getPageSource().contains("Errors list")) {
                                stage3Item.setStage(stage3Item.getStage() + Constants.ERROR_STATUS);
                            } else {
                                stage3Item.setStage(stage3Item.getStage() + "COMPLETED");
                            }
                        }
                        return stage3Item;
                    }
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
        return null;
    }

    private void goToLivePage() {
        try {
            driver.get("https://www.parimatch.com/en/live.html");
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

            BetDomUtils.setAttribute(driver, loginInput, "value", "");
            BetDomUtils.setAttribute(driver, passwordInput, "value", "");
            okButton.click();
        } catch (Exception e) {
            System.out.println("Error with find login inputs");
            e.printStackTrace();
        }
    }
}