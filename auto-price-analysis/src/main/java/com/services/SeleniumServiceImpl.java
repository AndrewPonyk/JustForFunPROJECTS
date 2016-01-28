package com.services;

import com.dto.Advert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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

                try{
                    if(advert.findElements(By.cssSelector(".item.ticket-title")).size()>0){
                        
                        Advert advertItem = new Advert().
                            setName(advert.findElements(By.cssSelector(".item.ticket-title")).get(0).getText()).
                            setCity(advert.findElements(By.cssSelector(".location a")).get(0).getText()).
                            setYear(extractYear(advert.findElements(By.cssSelector(".item.ticket-title")).get(0).getText())).
                            setPrice(extractInteger(advert.findElements(By.cssSelector(".price-ticket .size16 strong")).get(0).getText())).
                            setMileage(extractInteger(advert.findElements(By.cssSelector(".characteristic .item-char")).get(0).getText())).
                            setDescription(advert.findElements(By.cssSelector(".descriptions-ticket")).get(0).getText()).
                            setAutoriaId(extractAutoriaId(advert.findElements(By.cssSelector(".ticket-title .address")).get(0).getAttribute("href")));
                        
                        
                                                
                        
                        if(!allCars.contains(advertItem) && advertItem.name != null && advertItem.name.length() > 0 ){
                            FileSystemServiceImpl.saveImage(advertItem, advert.findElements(By.cssSelector(".ticket-photo img")).get(0).getAttribute("src"));
                            advertItem.imageSaved = true;    
                            allCars.add(advertItem);                            
                        }
                    }
                }catch (Exception e) {
                    //e.printStackTrace();
                    //System.err.println(e.getMessage() + " For " + advert);
                }

            }
        );
        //closeBrowser();

        return allCars;
    }

    public Integer extractYear (String name){
        String result = name.replaceAll("[^0-9]", "");
        if(result.length() > 4){
            result = result.substring(result.length() - 4);
        }
        try {
            return  Integer.parseInt(result);
        }catch (Exception e){

        }
        return 0;
    }

    public Integer extractInteger(String price){
        try{
            return  Integer.parseInt(price.replaceAll("[^0-9]", ""));
        }catch (Exception e){

        }
        return 0;
    }
    
    public String extractAutoriaId(String str){
        int beginIndex = str.lastIndexOf("_");
        int endIndex = str.lastIndexOf(".");
        
        return str.substring(beginIndex+1, endIndex);
    }
    
}
