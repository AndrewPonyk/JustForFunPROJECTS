package javacodegeeks;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;

/**
 * Created by andrii on 06.03.17.
 */
public class JavaCodegeeksBrowser {
    static {
        System.setProperty("webdriver.gecko.driver", "/home/andrii/Programs/geckodriver");
    }
    static WebDriver driver =   new FirefoxDriver();
    static String url = "https://www.javacodegeeks.com/page/";

    public static void readArticleTitles(){
        WebElement closePopup;

        for(int i = 840;i>0;i--){
            System.out.println("------Page: " + i);
            driver.get(url+ i);
            try{
                closePopup = driver.findElement(By.className("snp-close-link"));
                if(closePopup != null){
                    closePopup.click();
                }

            }catch (Exception e){

            }


            List<WebElement> posts = driver.findElements(By.className("post-title"));
            posts.forEach(e->{System.out.println(e.getText());});
        }
    }
}
