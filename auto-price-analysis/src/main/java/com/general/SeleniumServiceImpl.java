package com.general;

import com.dto.Advert;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrii on 24.01.16.
 */
public class SeleniumServiceImpl implements SeleniumService {
    WebDriver driver = new FirefoxDriver();
    WebDriverWait wait = new WebDriverWait(driver, 15);

    private void open(String url) {
         driver.get(url);
    }

    private void closeBrowser(){
        driver.quit();
    }

    @Override
    public List<Advert> getAllCars(String carUrl) {
        final List<Advert> allCars = new ArrayList<>();
        open(carUrl);

        By carsCountSelector = By.cssSelector("#resultsCount .count");
        wait.until(ExpectedConditions.visibilityOfElementLocated(carsCountSelector));
        System.out.println(driver.findElement(carsCountSelector).getText());

        List<WebElement> carsAdverts = driver.findElements(By.cssSelector(".ticket-item"));
        carsAdverts.forEach(advert -> {
            allCars.add(new Advert().
                            setName(advert.findElements(By.cssSelector(".item.ticket-title")).get(0).getText()).
                            setCity(advert.findElements(By.cssSelector(".location a")).get(0).getText()).
                            setPrice(advert.findElements(By.cssSelector(".price-ticket .size16")).get(0).getText())
            );

        }
        );
        //closeBrowser();

        return allCars;
    }
}
