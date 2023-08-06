package dj;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;

import static dj.DJBrowser.driver;

public class CanRetriever {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Cand retriever");
        String url = "https://djinni.co/developers/?title=Java&location=lviv&exp_from=5&page=";

        driver.get(url);
        List<WebElement> pages = driver.findElements(By.cssSelector("li.page-item"));
        int totalpages = Integer.parseInt(pages.get(pages.size() - 2).getText()); // not minus 1 because last element is >> arrow, so last page is size-2

        int counter = 1;
        for (int i = 1; i <= totalpages; i++) {
            driver.get(url+i);
            List<WebElement> candidates = driver.findElements(By.cssSelector("div.card-body"));

            List<WebElement> showMoreButtons = driver.findElements(By.cssSelector(".show-more-btn a"));
            Thread.sleep(100);
            showMoreButtons.stream().forEach(elem->{
                if(!elem.isDisplayed()){
                    return;
                }
                elem.click();

            });

            for (int j = 0; j < candidates.size(); j++) {
                System.out.println(counter + ")" +candidates.get(j).getText());
                System.err.println("\n "+ counter +"====================================================================");
                counter++;
            }

        }
        driver.quit();
    }
}