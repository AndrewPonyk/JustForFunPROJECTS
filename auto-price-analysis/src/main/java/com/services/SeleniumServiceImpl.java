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
import java.util.function.BinaryOperator;

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
    public List<Advert> getAllCars(String carUrl,final String model) {
        final List<Advert> allCars = new ArrayList<>();

        By carsCountSelector = By.cssSelector("#resultsCount .count");

        Integer count = -1000;
        Integer currentPage = 0;

        try{
            open(carUrl);
            wait.until(ExpectedConditions.visibilityOfElementLocated(carsCountSelector));
            System.out.println(driver.findElement(carsCountSelector).getText());
            count = Integer.parseInt(driver.findElement(carsCountSelector).getText());
        }catch (Exception e){
            closeBrowser();
            e.printStackTrace();
            return allCars;
        }

        for(int i = 0; i <= count/100; i++){
            if(i>0){
                try {
                    open("http://google.com");
                    Thread.sleep(4000);
                    System.out.println("CALLLING " + carUrl + "&page=" + i);
                    open(carUrl + "&page=" + i);
                    wait.until(ExpectedConditions.visibilityOfElementLocated(carsCountSelector));
                } catch (Exception e) {
                    closeBrowser();
                    e.printStackTrace();
                    return allCars;
                }
            }


            List<WebElement> carsAdverts = driver.findElements(By.cssSelector(".ticket-item"));
            System.out.println(i +"====:Size on page " + carsAdverts.size());
            int[] counter = new int[1];
            counter[0] = 0;
            carsAdverts.forEach(advert -> {
                        System.out.println("Counter " + (counter[0]++));
                        try{
                            List<WebElement> elements = advert.findElements(By.cssSelector(".item.ticket-title"));

                            if(elements.size()>0 && elements.get(0).getText().length()>0){

                                Advert advertItem = new Advert().
                                        setName(advert.findElements(By.cssSelector(".item.ticket-title")).get(0).getText()).
                                        setCity(advert.findElements(By.cssSelector(".location a")).get(0).getText()).
                                        setYear(extractYear(advert.findElements(By.cssSelector(".item.ticket-title")).get(0).getText())).
                                        setPrice(extractInteger(advert.findElements(By.cssSelector(".price-ticket .size16 strong")).get(0).getText())).
                                        setMileage(extractInteger(advert.findElements(By.cssSelector(".characteristic .item-char")).get(0).getText())).
                                        setDescription(advert.findElements(By.cssSelector(".descriptions-ticket")).get(0).getText()).
                                        setAutoriaId(extractAutoriaId(advert.findElements(By.cssSelector(".ticket-title .address")).get(0).getAttribute("href"))).
                                        setImageUrl(advert.findElements(By.cssSelector(".ticket-photo img")).get(0).getAttribute("src")).
                                        setModel(model);

                                System.out.println("Counter. " + counter[0]);
                                System.out.println(advertItem);
                                if(!allCars.contains(advertItem) && advertItem.name != null && advertItem.name.length() > 0 ){
                                    allCars.add(advertItem);
                                    System.out.println(allCars.size() + ";;;");
                                }else {
                                    System.out.println("==== DOnt add :" + advertItem) ;
                                }
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                            //System.err.println(e.getMessage() + " For " + advert);
                        }

                    }
            );
        }
        final Integer averageModelPriceOnDate = (int)allCars.stream().mapToInt(e->e.price).average().getAsDouble();
        allCars.forEach(car->{
            car.setAveragePriceOnThisDate(averageModelPriceOnDate);
        });
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
