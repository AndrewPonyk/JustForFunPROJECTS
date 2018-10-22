package com.ap.monitor;

import com.ap.model.SyncResult;
import com.ap.utils.Constants;
import com.ap.wincheckers.TennisWinChecker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class ResyltSync {

    private static ChromeDriver driver;
    private static String RESULTS_PAGE = "https://www.parimatch.com/en/res.html?&Date=";

    static {
        System.setProperty("webdriver.chrome.driver", Constants.driver());
        System.setProperty("webdriver.gecko.driver", "/home/andrii/Programs/geckodriver");
    }

    public static List<SyncResult> getResults() {
        driver = new ChromeDriver();
        List<SyncResult> result = new LinkedList<>();
        LocalDateTime now = LocalDateTime.now();
        driver.get(RESULTS_PAGE + now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "&SK=0");

        String resultsTable = driver.findElements(By.cssSelector("#z_contentw")).get(0).getAttribute("outerHTML");
        Document resultsTableElement = Jsoup.parse(resultsTable);
        Elements resultsRows = resultsTableElement.select("tbody.row1");

        try {
            driver.quit();
        } catch (Exception e) {
            System.out.println("Can not quit results web driver");
        }

        resultsRows.forEach(resultRow -> {
            try {
                Elements names = resultRow.select(".Names");
                Elements otherCells = resultRow.select(".Mono");
                if (names.size() == 2) {
                    String name1 = names.get(0).text();
                    String name2 = names.get(1).text();
                    if (otherCells.size() > 0) {
                        String gameResult = otherCells.get(otherCells.size() - 1).text().replaceAll(":", "-");
                        result.add(new SyncResult(name1.trim(),
                                name2.trim(),
                                gameResult.trim()));
                    }
                }
            } catch (Exception e) {
                System.out.println("Can not parse resultRow");
            }
        });

        return result;
    }


    public static void main(String[] args) {
        getResults().forEach(System.out::println);
    }


}