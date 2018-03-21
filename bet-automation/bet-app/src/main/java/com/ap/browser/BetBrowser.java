package com.ap.browser;

import com.ap.dao.BetRepo;
import com.ap.dao.BetRepoJdbc;
import com.ap.model.BetItem;
import com.ap.utils.BetDomUtils;
import com.ap.utils.Constants;
import com.ap.utils.RegexUtils;
import com.ap.utils.ValidationUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ap.utils.Constants.COMPLETED;
import static com.ap.utils.Constants.ERROR_STATUS;
import static com.ap.utils.Constants.SKIPPED_STATUS;

public class BetBrowser {
    Logger logger = LoggerFactory.getLogger(BetBrowser.class);
    Random random = new Random();

    public static WebDriver driver;
    public static BetRepo betRepo = new BetRepoJdbc();
    public static WebDriverWait wait;
    public static int monitorsWithoutChanges = 0;


    static {
        System.setProperty("webdriver.gecko.driver", "/home/andrii/Programs/geckodriver");
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");

        System.setProperty("webdriver.chrome.driver", Constants.driver());
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 9);
    }

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
                System.out.println(bet + ": " + "try again, but only 1 time");
                bet = checkAndBet();
            }

            if (bet != null) {
                // update in db
                betRepo.markBetAsCompleted(bet);
            }

            try {
                //refresh page once per hour
                LocalDateTime now = LocalDateTime.now();
                if ((now.getMinute() == 7 || now.getMinute() == 21 || now.getMinute() == 47)
                        && now.getSecond() > 40) {
                    goToLivePage();
                    login();
                    System.out.println("Performin refresh twice per hour " + now);
                }
                Thread.sleep(9500);
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
        }
    }

    private void parseAndSaveBets() {
        try {
            WebElement upWebElement = driver.findElements(By.cssSelector("i.up")).get(0);
            if (upWebElement != null && upWebElement.getText().trim().length() > 0) {
                monitorsWithoutChanges = 0;
                System.out.println(upWebElement.getText() + " << Up element");
            } else {

                monitorsWithoutChanges++;
            }
        } catch (Exception e) {
            monitorsWithoutChanges++;
        }

        System.out.println("Monitor without changes >>" + monitorsWithoutChanges);
        if (monitorsWithoutChanges >= 10) {
            monitorsWithoutChanges = 0;
            goToLivePage();
        }

        try {
            RegexUtils.currentTime = LocalDateTime.now();
            String betTable = driver.findElements(By.cssSelector("#inplay")).get(0).getAttribute("outerHTML");
            Document betTableElement = Jsoup.parse(betTable);
            Elements betRows = betTableElement.select("#inplay table.dt");
            logger.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

            LinkedList<BetItem> betItems = new LinkedList<>();
            final AtomicInteger countSkip = new AtomicInteger(0);
            betRows.forEach(row -> {
                try {
                    Element win1Element = row.select("tr td:nth-child(3)").first();
                    Element win2Element = row.select("tr td:nth-child(5)").first();
                    String sport = row.parent().parent().parent().select("p.sport").first().text();
                    String competition  = row.parent().previousElementSibling().text();
                    Elements liveStreams = row.select(".ics");
                    //
                    String link = "https://www.parimatch.com/en/"
                            + row.select("tr td:nth-child(2) a").get(0).attr("href");
                    if (ValidationUtils.validateSport(sport) && ValidationUtils.validateCompetitions(competition)) {
                        String betText = row.html();
                        if (!betText.toLowerCase().contains("corners")) {
                            betItems.add(RegexUtils.parseBetItem(sport, betText,
                                    win1Element.text(), win2Element.text(), link, competition, liveStreams));
                        }
                    }

                } catch (Exception wrondInvisibleRow) {
                    //this slow down app, so comment
                    //logger.info(wrondInvisibleRow.getMessage());
                    countSkip.incrementAndGet();
                }
            });
            logger.info("LENGHT = " + betRows.size());
            logger.info("Skipped items:: " + countSkip.get());
            // slow down app
            //final StringBuilder betItemsString = new StringBuilder();
//            betItems.forEach(e -> {
//                betItemsString.append(e.toString());
//                betItemsString.append("=====================\n");
//            });
//            logger.info(betItemsString.toString());
            betRepo.saveUpdateItems(betItems);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    private BetItem checkAndBet() {
        Integer lastBetStatus = (Integer) betRepo.getLastPerformedBet().get("WIN_LAST_BET");
        String lastPerformedBetTitle = (String) betRepo.getLastPerformedBet().get("LAST_BET_TITLE");
        if (lastBetStatus == 0) {
            return null;
        }

        BetItem possibleComeBackItem = null;
        //stage3 item code: was removed 24.01.2018
        List<BetItem> possibleComeBackItems = betRepo.getBetsWhereComebackIsPossile();

        if (!possibleComeBackItems.isEmpty()) {
            String parentWindowHandler = "";
            possibleComeBackItem = possibleComeBackItems.get(random.nextInt(possibleComeBackItems.size()));
            logger.info("FOUND POSSIBLE COMEBACK" + possibleComeBackItem.getTitle());

            if(lastBetStatus == -1 ){
                // skipping algorithm
                // skip in such situations
                // 1) no history is available
                // 2) last bet win, and in 3 lasts there is at least one lose

                LinkedList<Pair<Integer, BetItem>> last5Possible = betRepo.getAllComebackItemsFromHistory(4);
                if(last5Possible.isEmpty()){
                    if(!possibleComeBackItem.getNotes().contains(SKIPPED_STATUS)){
                        possibleComeBackItem.setNotes(possibleComeBackItem.getNotes()+SKIPPED_STATUS+"no history avail");
                    }
                    return possibleComeBackItem;
                }

                //check if 3 last bets win (iw win then bet)
                Boolean winLastThreeBets = true;
                Boolean winAtLeastOneOfFour = false;

                Pair<Integer, BetItem> lastPastPossibleComeback =
                        last5Possible.get(0);
                Pair<Integer, BetItem> lastPastPossibleComeback2 =
                        last5Possible.get(1);
                Pair<Integer, BetItem> lastPastPossibleComeback3 =
                        last5Possible.get(2);
                Pair<Integer, BetItem> lastPastPossibleComeback4 =
                        last5Possible.get(3);


                String[] lastPastPossibleComebackTitle = lastPastPossibleComeback.getRight().getTitle().split("-");

                Integer lastPossibleComebackWinner = lastPastPossibleComeback.getLeft();


                winLastThreeBets = (lastPossibleComebackWinner != -1 &&  lastPastPossibleComeback.getRight().getStage().contains("er"+lastPossibleComebackWinner))

                        && ((lastPastPossibleComeback2.getLeft() != -1 &&  lastPastPossibleComeback2.getRight().getStage().contains("er"+lastPastPossibleComeback2.getLeft())))

                        && ((lastPastPossibleComeback3.getLeft() != -1 &&  lastPastPossibleComeback3.getRight().getStage().contains("er"+lastPastPossibleComeback3.getLeft())));



                winAtLeastOneOfFour = (lastPossibleComebackWinner != -1 &&  lastPastPossibleComeback.getRight().getStage().contains("er"+lastPossibleComebackWinner))

                        || ((lastPastPossibleComeback2.getLeft() != -1 &&  lastPastPossibleComeback2.getRight().getStage().contains("er"+lastPastPossibleComeback2.getLeft())))

                        || ((lastPastPossibleComeback3.getLeft() != -1 &&  lastPastPossibleComeback3.getRight().getStage().contains("er"+lastPastPossibleComeback3.getLeft())))

                        || ((lastPastPossibleComeback4.getLeft() != -1 &&  lastPastPossibleComeback4.getRight().getStage().contains("er"+lastPastPossibleComeback4.getLeft())));


                if(!winLastThreeBets && (lastPossibleComebackWinner == -1 ||  lastPastPossibleComeback.getRight().getStage().contains("er"+lastPossibleComebackWinner))){
                    //set skipped
                    if(!possibleComeBackItem.getNotes().contains(SKIPPED_STATUS) && !possibleComeBackItem.getNotes().contains(COMPLETED) ){
                        possibleComeBackItem.setNotes(possibleComeBackItem.getNotes()+SKIPPED_STATUS);
                    }
                     return possibleComeBackItem;
                }

                if(!winAtLeastOneOfFour ){
                    if(!possibleComeBackItem.getNotes().contains(SKIPPED_STATUS) && !possibleComeBackItem.getNotes().contains(COMPLETED) ){
                        possibleComeBackItem.setNotes(possibleComeBackItem.getNotes()+SKIPPED_STATUS);
                    }
                    return possibleComeBackItem;

                }

                //skip if last bet contains current bet
                if((lastPerformedBetTitle.contains(lastPastPossibleComebackTitle[0].trim()) ||
                        lastPerformedBetTitle.contains(lastPastPossibleComebackTitle[1].trim()))){
                    if(!possibleComeBackItem.getNotes().contains(SKIPPED_STATUS) && !possibleComeBackItem.getNotes().contains(COMPLETED)){
                        possibleComeBackItem.setNotes(possibleComeBackItem.getNotes()+SKIPPED_STATUS);
                    }
                    return possibleComeBackItem;
                }



            }

            Integer possibleComebackPlayer = 0;
            if (possibleComeBackItem.getStage().startsWith("player1")) {
                possibleComebackPlayer = 1;
            } else if (possibleComeBackItem.getStage().startsWith("player2")) {
                possibleComebackPlayer = 2;
            }

            try {
                List<WebElement> betRows = driver.findElements(By.xpath(
                        "//div[@id='inplay']//table[contains(@class,'dt') and contains(normalize-space(),'" +
                                possibleComeBackItem.getTitle().substring(0, 5) + "')]"));

                logger.info(":::" + betRows.size());
                if (betRows.isEmpty()) {
                    throw new Exception("Bet is not present anymore");
                }

                for (WebElement row : betRows) {
                    String rowText = row.getText().replace("<small>", "").replace("</small>", "").replaceAll("'", "");
                    if (rowText.contains(possibleComeBackItem.getTitle())) {

                        WebElement winElement = possibleComebackPlayer == 1 ? row.findElement(By.cssSelector("tr td:nth-child(3)")) :
                                row.findElement(By.cssSelector("tr td:nth-child(5)"));
                        winElement.click();
                        Thread.sleep(100);
                        driver.findElement(By.cssSelector(".btn_orange")).click();

                        parentWindowHandler = driver.getWindowHandle();
                        String subWindowHandler = "";

                        Set<String> handles = driver.getWindowHandles();
                        Iterator<String> iterator = handles.iterator();
                        while (iterator.hasNext()) {
                            String next = iterator.next();
                            if (!next.equals(parentWindowHandler)) {
                                subWindowHandler = next;
                            }
                        }

                        driver.switchTo().window(subWindowHandler); // switch to popup window
                        logger.info("SUBWINDOW:" + driver.getCurrentUrl());
                        Thread.sleep(150);
                        logger.info("-----------------------------------------------------------");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name=sums]")));
                        WebElement sumElement = driver.findElement(By.cssSelector("input[name=sums]"));
                        WebElement currBalanceElement = driver.findElement(By.cssSelector("#ownerInfo td b"));
                        Double currBalance = Double.parseDouble(currBalanceElement.getText().replaceAll("UAH", "").trim());

                        if (currBalance < Constants.MIN_BET  ) {
                            if(!possibleComeBackItem.getStage().contains(ERROR_STATUS)){
                                possibleComeBackItem.setStage(possibleComeBackItem.getStage() + ERROR_STATUS + "No_enough_money");
                            }
                            return possibleComeBackItem;
                        }

                        Double betSum = 10000D;
                        Double currentCoef = ValidationUtils.getCurrentCoef(driver);
                        System.out.println("BET COEFF: " + currentCoef);
                        if (lastBetStatus == 1) {
                            // win last bet, so set base bet
                            betSum = currBalance * Constants.FIRST_BET_IN_PERCENTS;

                            System.out.println("Setting bet sume = " + betSum);
                        } else if (lastBetStatus == -1) {
                            //lose last bet, need bigger bet
                            betSum = betRepo.getLastLoseBetsSum() / (currentCoef - 1) * Constants.PROFIT_RATIO;
                            if (betSum < Constants.MIN_BET) {
                                betSum = Constants.MIN_BET * Constants.PROFIT_RATIO;
                            }
                        } else {
                            return null;
                        }

                        if (betSum < Constants.MIN_BET) {
                            betSum = Constants.MIN_BET;
                        }
                        if (betSum > currBalance) {
                            betSum = currBalance;
                        }

                        //temppppppppppppppppppppppppp
                        betSum = currBalance* 0.012 > 3.0 ? currBalance* 0.012 : 3.0;// temppppppp
                        if(lastBetStatus == -1){
                            betSum = betRepo.getLastLoseBetsSum() * 1.56 > currBalance ?
                            currBalance: betRepo.getLastLoseBetsSum() * 1.56;

                            if(1 / (currentCoef - 1) * Constants.PROFIT_RATIO < 1.56){
                                betSum = betRepo.getLastLoseBetsSum() / (currentCoef - 1) * Constants.PROFIT_RATIO;
                            }

                            if(betSum> currBalance){
                                betSum=currBalance;
                            }

                        }

                        BetDomUtils.setAttribute(driver, sumElement, "value", "" + betSum);
                        if (driver.getPageSource().contains("Errors list") &&
                                !driver.getPageSource().contains("have been changed")) {
                            if(!possibleComeBackItem.getStage().contains(ERROR_STATUS)){
                                possibleComeBackItem.setStage(possibleComeBackItem.getStage() + ERROR_STATUS);
                            }


                        } else {
                            Thread.sleep(100);

                            try {
                                // get current progress money
                                String progressMoney = driver.findElements(By.cssSelector("#ownerInfo tr")).get(1)
                                        .findElements(By.cssSelector("td")).get(6).getText();
                                System.out.println("Progress money:" + progressMoney);
                                if(!progressMoney.trim().startsWith("0")){
                                    return null;
                                }
                            } catch (Exception e) {
                                System.out.println("Can not detect progress money");
                            }

                            if (currentCoef < Constants.COMEBACK_PERFORM_BET_BOUND[0] ||
                                    currentCoef > Constants.COMEBACK_PERFORM_BET_BOUND[1]) {
                                return null;
                            }


                            driver.findElement(By.cssSelector(".btn_orange")).click();
                            // looks like click waits until bet is done, but for sure I added this sleep
                            Thread.sleep(2700);

                            if (driver.getPageSource().contains("Errors list")) {
                                if(!possibleComeBackItem.getStage().contains(ERROR_STATUS)){
                                    possibleComeBackItem.setStage(possibleComeBackItem.getStage() + ERROR_STATUS);
                                }

                            } else {
                                betRepo.updateCurrentBetStatus(0, 0.0, 0.0, "<epmty>");
                                possibleComeBackItem.setNotes(possibleComeBackItem.getNotes() + COMPLETED);
                                System.out.println("Set COMPLETED notes:" + possibleComeBackItem.getTitle());
                                possibleComeBackItem.setStage(possibleComeBackItem.getStage() + "COMPLETED");
                            }
                        }
                        return possibleComeBackItem;
                    }
                    possibleComeBackItem.setStage(possibleComeBackItem.getStage() + "ERROR203Line");
                }
            } catch (Exception e) {
                logger.info("Error during checkAndBet" + e.getMessage());
                if(possibleComeBackItem!=null && possibleComeBackItem.getStage()!=null && !possibleComeBackItem.getStage().contains(ERROR_STATUS)){
                    possibleComeBackItem.setStage(possibleComeBackItem.getStage() + ERROR_STATUS);
                }
                return possibleComeBackItem;
            } finally {
                if (parentWindowHandler != null && !parentWindowHandler.isEmpty()) {
                    if(!driver.getCurrentUrl().contains("live.html")){
                        logger.info("Close window:::" + driver.getCurrentUrl());
                        driver.close();
                        driver.switchTo().window(parentWindowHandler);
                    }
                }
            }
        }
        // Big code duplication, will resolve in future! (removed 24.01.2018)
        // DISABLE THIS SMALL BETS, NO SENse

        return possibleComeBackItem;
    }

    private void goToLivePage() {
        try {
            //if(!driver.getCurrentUrl().contains("live.html")){
            driver.get("https://www.parimatch.com/en/live.html");
            //}
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

    }

    private void login() {
        try {
            driver.get(Constants.LIVE_URL);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login")));
            WebElement loginButton = driver.findElement(By.className("login"));
            loginButton.click();
        } catch (Exception e) {
            logger.info("Click login error or we are already logged in");
            // we are logged
            return;
        }

        try {
            WebElement loginInput = (new WebDriverWait(driver, 10)).
                    until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='username']")));
            WebElement passwordInput = driver.findElement(By.cssSelector("input[name='passwd']"));
            WebElement okButton = driver.findElement(By.cssSelector(".btn_orange.ok"));

            BetDomUtils.setAttribute(driver, loginInput, "value", Constants.PAR_EMAIL);
            BetDomUtils.setAttribute(driver, passwordInput, "value", Constants.PAR_PASS);
            okButton.click();
        } catch (Exception e) {
            logger.info("Error with find login inputs");
            e.printStackTrace();
        }
    }
}