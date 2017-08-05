package packtbookslist;

import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class PacktBooksListProgram {

    public static void main(String[] args) {
        new PacktBooksListProgram().runApp();
        System.out.println(new Date().toString());
    }

    public void runApp() {
        System.out.println("Listing of all packt books");
        int bookCounter = 1;
        int offsetCounter = 0;
        String url;
        WebDriver driver = new FirefoxDriver();

        for (int i = 1; i <= 103; i++) {

            System.out.println("Page " + i + "===============================================");
            url = "https://www.packtpub.com/all?search=&offset=" +
                    +offsetCounter
                    + "&rows=48&sort=ss_cck_field_date_of_publication+desc"; // BOOKS AND VIDEO

            driver.get(url);
            System.out.println(url);
            List<WebElement> books = driver.findElements(By.cssSelector(".book-block"));

            for (WebElement item : books) {
                WebElement titleElement = item.findElement(By.cssSelector(".book-block-title"));
                WebElement bookLink = item.findElement(By.cssSelector("a"));
                System.out.println(bookCounter + ") " + titleElement.getText() + " ::" + bookLink.getAttribute("href"));
                bookCounter++;
            }
            offsetCounter += 48;
        }
    }
}
