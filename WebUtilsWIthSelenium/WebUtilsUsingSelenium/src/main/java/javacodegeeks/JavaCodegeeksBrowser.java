package javacodegeeks;

import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * Created by andrii on 06.03.17.
 */
public class JavaCodegeeksBrowser {
    static {
        System.setProperty("webdriver.gecko.driver", "/home/andrii/Programs/geckodriver");

        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
    }

    //static WebDriver driver =   new FirefoxDriver(); // works only for selenium 3.0.0 !!!!
    static WebDriver driver =   new HtmlUnitDriver();
    static String url = "https://www.javacodegeeks.com/page/";
    static String examplesUrl = "https://examples.javacodegeeks.com/page/";

    public static void readArticleTitles(){
        WebElement closePopup;
        final AtomicInteger counter = new AtomicInteger(1);

        for(int i = 240;i>0;i--){
            System.out.println("------Page: " + i);
            driver.get(examplesUrl+ i);
            try{
                closePopup = driver.findElement(By.className("snp-close-link"));
                if(closePopup != null){
                    closePopup.click();
                }
            }catch (Exception e){

            }


            List<WebElement> posts = driver.findElements(By.className("post-title"));
            posts.forEach(e -> {
                System.out.println(new StringBuilder("").append(counter.incrementAndGet())
                        .append(") ").append(e.getText()));
            });
        }
    }
}
