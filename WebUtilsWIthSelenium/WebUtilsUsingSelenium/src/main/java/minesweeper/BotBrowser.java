package minesweeper;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;

/**
 * Created by andrii on 29.01.17.
 */
public class BotBrowser {
    static {
        System.setProperty("webdriver.gecko.driver", "/home/andrii/Programs/geckodriver");
    }

    static WebDriver driver =   new  FirefoxDriver();
    static String gameUrl = "http://minesweeper.odd.su/";
    static WebElement canvas;

    public static void openPageAndStartGame() throws InterruptedException {
        driver.get(gameUrl);
        WebElement inputName = driver.findElement(By.id("name-dialog-name"));
        inputName.sendKeys("Andrii9999");
        inputName.sendKeys(Keys.RETURN);
        Thread.sleep(3000);
        canvas = driver.findElement(By.tagName("canvas"));
        System.out.println(canvas);

        new Actions(driver).moveToElement(canvas).moveByOffset(115, 115).click().build().perform();

    }

}
