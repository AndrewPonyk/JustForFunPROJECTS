package packtbookslist;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class PacktBooksListProgram {

    static {
            System.setProperty("webdriver.chrome.driver", driver());
    }

    public static void main(String[] args) {
        new PacktBooksListProgram().runApp();
        System.out.println(new Date().toString());
    }

    public void runApp() {
        System.out.println("Listing of all packt books");
        int bookCounter = 1;
        int offsetCounter = 0;
        String url;
        WebDriver driver = new HtmlUnitDriver();

        for (int i = 1; i <= 121; i++) {

            System.out.println("Page " + i + "===============================================");
            url = "https://www.packtpub.com/all?search=&offset=" +
                    +offsetCounter
                    + "&rows=48&sort=ss_cck_field_date_of_publication+desc"; // BOOKS AND VIDEO

            driver.get(url);
            System.out.println(url);
            List<WebElement> books = driver.findElements(By.cssSelector(".book-block"));

            for (WebElement item : books) {
                WebElement titleElement = item.findElement(By.cssSelector(".book-block-title"));
                WebElement releaseDate = item.findElement(By.cssSelector(".book-block-overlay-release-date"));


                List<WebElement> productLength = item.findElements(By.cssSelector(".book-block-overlay-product-length"));
                String productLengthText = productLength.size() > 0 ? productLength.get(0).getAttribute("innerHTML") : "";

                WebElement bookLink = item.findElement(By.cssSelector("a"));
                System.out.println(bookCounter + ") " + titleElement.getText() +
                        " -- [" + releaseDate.getAttribute("innerHTML") +"]" + "[length:" + productLengthText + "]" +"::" + bookLink.getAttribute("href"));
                bookCounter++;
            }
            offsetCounter += 48;
        }
    }

    public static String driver(){
        File driver = new File("C:\\tmp\\chromedriver.exe");
        if(driver.exists()){
            return "C:\\tmp\\chromedriver.exe";
        } else {
            return "/home/andrii/Programs/chromedriver";
        }
    }
}
