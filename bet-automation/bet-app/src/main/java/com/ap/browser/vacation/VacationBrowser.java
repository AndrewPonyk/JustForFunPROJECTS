package com.ap.browser.vacation;

import com.ap.utils.Constants;
import com.ap.utils.JavaCoreSendMailUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class VacationBrowser {
    public static FirefoxDriver driver;
    public static WebDriverWait wait;

    static {
        System.setProperty("webdriver.gecko.driver", "/home/andrii/Programs/geckodriver");
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
    }

    public static void main(String[] args) {
        VacationBrowser vacationBrowser = new VacationBrowser();
        vacationBrowser.startVacationBrowser();

    }

    public void startVacationBrowser(){
        try {
            driver = new FirefoxDriver();
            wait = new WebDriverWait(driver, 9);
            List<List<List<String>>> fullResult = new ArrayList<>();
            for (String item : SEARCH_URLS){
                List<List<String>> country = new ArrayList<>();
                driver.get("http://google.com");
                driver.get(item);
                Thread.sleep(3000);

                List<WebElement> elements = driver.findElements(By.cssSelector(".os-result"));
                for (int i = 0; i < elements.size() && i < 10; i++) {
                    WebElement tour = elements.get(i);
                    TourDto tourDto = new TourDto();
                    tourDto.setNameAndCity(tour.findElement(By.cssSelector(".os-result-title")).getText() +
                                    tour.findElement(By.cssSelector(".os-result-geo_locale")).getText()
                                    .replaceAll("Подивитись на карті", "")
                    );
                    tourDto.setDepartureAndCity(tour.findElements(By.cssSelector(".os-offer-price .os-offer-price_date")).get(0).getText()
                            + " : "
                            + new Select(driver.findElement(By.cssSelector(".os-block_select"))).getFirstSelectedOption().getText());
                    tourDto.setDaysCount(tour.findElements(By.cssSelector(".table-row.headline .table-head")).get(1).getText());
                    tourDto.setFood(tour.findElements(By.cssSelector(".food.table-cell")).get(0).getText());

                    List<WebElement> feedback = tour.findElements(By.cssSelector(".os-result_hrating"));
                    tourDto.setFeedbacks(feedback.size() > 0 ? feedback.get(0).getText():"");

                    tourDto.setPrice(tour.findElements(By.cssSelector(".os-offer-price .os-offer-price_value")).get(0).getText());
                    tourDto.setDesc(substringFirst85(tour.findElements(By.cssSelector(".os-result-description")).get(0).getText()));
                    tourDto.setLink(tour.findElements(By.cssSelector(".os-result-title_link")).get(0).getAttribute("href"));
                    country.add(tourDto.toList());
                }

                fullResult.add(country);

            }
            JavaCoreSendMailUtils.sendTwoDimTables(Constants.BET_EMAIL, "Vacation 2018", fullResult,
                    Constants.BET_EMAIL, Constants.BET_PASSWORD);

           JavaCoreSendMailUtils.sendTwoDimTables("anothereiaml@gmail.com", "Vacation 2018", fullResult,
                   Constants.BET_EMAIL, Constants.BET_PASSWORD);

        }catch (Exception e){
            System.out.println("Vacation browser exception " + e.getMessage());
            e.printStackTrace();
        } finally {
            driver.quit();
        }


    };

    public static final String[] SEARCH_URLS = {
            "http://www.lembergtravel.com/poshuk-turiv.html#!page=form&f=5&t=%D0%A2%D1%83%D0%BD%D1%96%D1%81&i=114&y=country&c=2018-07-25&v=2018-07-31&l=8&p=2&tc=&n=0&g=1&d=1397&o=uai%2Cai&st=5&pf=500&pt=100000&rt=0%2C10&e=&sr=&r=air%2Cbus%2Ctrain%2Cship&w=&ex=1",
            "http://www.lembergtravel.com/poshuk-turiv.html#!f=5&t=%D0%84%D0%B3%D0%B8%D0%BF%D0%B5%D1%82&i=43&y=country&c=2018-07-25&v=2018-07-31&l=8&p=2&tc=&n=0&g=1&d=1397&o=uai%2Cai&st=5&pf=500&pt=100000&rt=0%2C10&e=one_line_beach&sr=&r=air%2Cbus%2Ctrain%2Cship&w=&ex=1&page=form",
            "http://www.lembergtravel.com/poshuk-turiv.html#!f=5&t=%D0%9A%D1%96%D0%BF%D1%80&i=54&y=country&c=2018-07-25&v=2018-07-31&l=8&p=2&tc=&n=0&g=1&d=1397&o=uai%2Cai%2Cfb%2Chb&st=4&pf=500&pt=100000&rt=0%2C10&e=&sr=&r=air%2Cbus%2Ctrain%2Cship&w=&ex=1&page=form",
            "http://www.lembergtravel.com/poshuk-turiv.html#!f=5&t=%D0%86%D1%81%D0%BF%D0%B0%D0%BD%D1%96%D1%8F&i=49&y=country&c=2018-07-25&v=2018-07-31&l=8&p=2&tc=&n=0&g=1&d=1397&o=uai%2Cai%2Cfb%2Chb&st=4&pf=500&pt=100000&rt=0%2C10&e=&sr=&r=air%2Cbus%2Ctrain%2Cship&w=&ex=1&page=form"
    };

    public static String substringFirst85(String desc){
        if(desc.length() < 85){
            return desc;
        }

        return desc.substring(0, 85) + " ...";
    }


}
