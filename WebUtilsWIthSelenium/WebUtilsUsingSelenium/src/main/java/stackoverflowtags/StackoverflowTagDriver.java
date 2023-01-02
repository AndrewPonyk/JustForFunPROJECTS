package stackoverflowtags;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.List;

import static packtbookslist.PacktBooksListProgram.driver;


public class StackoverflowTagDriver {
    protected static RemoteWebDriver driver;

    static {
        System.setProperty("webdriver.chrome.driver", driver());
    }

    public static void retrieve(String url){
        driver = new ChromeDriver();

        url = "https://stackoverflow.com/tags?page=%d&tab=popular";
        int counter = 1;
        for (int i = 1; i <= 325; i++) {
            driver.get(String.format(url, i));
            final List<WebElement> boxes =
                    driver.findElements(By.cssSelector(".grid--item.s-card.js-tag-cell.d-flex.fd-column"));
            for (int j = 0; j < boxes.size(); j++) {
                String title = boxes.get(j).findElement(By.cssSelector(".post-tag")).getText();

                String desc = "";
                try { desc = boxes.get(j).findElement(By.cssSelector(".flex--item.fc-medium.mb12.v-truncate4")).getText();}catch (Exception e){}

                String numberOfQuestions = boxes.get(j).findElements(By.cssSelector(".fs-caption.fc-black-400 div")).get(0).getText();

                System.out.println(counter++ + ")" + title + "(" + numberOfQuestions +")" +":: " + desc);
            }
        }

    }

}
