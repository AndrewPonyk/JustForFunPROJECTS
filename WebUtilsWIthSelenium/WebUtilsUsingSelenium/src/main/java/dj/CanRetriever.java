package dj;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static dj.DJBrowser.driver;
import static dj.VacRetriever.persistImage;

public class CanRetriever {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Cand retriever");
        String url = "https://djinni.co/developers/?title=Java&location=lviv&exp_from=5&page=";

        driver.get(url);driver.manage().window().maximize();

        List<WebElement> pages = driver.findElements(By.cssSelector("li.page-item"));
        int totalpages = Integer.parseInt(pages.get(pages.size() - 2).getText()); // not minus 1 because last element is >> arrow, so last page is size-2

        int counter = 1;
        List<String> candLinks = new java.util.ArrayList<>();
        for (int i = 1; i <= 1; i++) {
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
                WebElement linkElement = candidates.get(j).findElement(By.cssSelector("a.profile"));
                candLinks.add(linkElement.getAttribute("href"));
                System.out.println(counter + ")" +candidates.get(j).getText());
                System.err.println("\n "+ counter +"====================================================================");
                counter++;
            }
        }


        counter = 1;
        int zoomLevel = 60;
        String jsScriptZoomOut = "document.body.style.zoom='" + zoomLevel + "%'; window.scrollBy(0, 50);";

        for (int i = 0; i < candLinks.size(); i++) {
            //iterate over all links and open them
            driver.get(candLinks.get(i));
            ((JavascriptExecutor) driver).executeScript(jsScriptZoomOut);
            WebElement candElement = driver.findElement(By.cssSelector("#content > div:nth-child(3)"));

            String title = driver.findElement(By.cssSelector("h1")).getText().replace("\\", "-").replace("/", "-");

            printPaddingAndMargin(driver, candElement);
            persistImage(zoomLevel, counter++, candElement, title);
        }

        driver.quit();
    }


        public static void printPaddingAndMargin(WebDriver driver, WebElement element) {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

            // Get the padding and margin values using JavaScript
            String paddingLeft = jsExecutor.executeScript("return window.getComputedStyle(arguments[0], null).getPropertyValue('padding-left');", element).toString();
            String paddingRight = jsExecutor.executeScript("return window.getComputedStyle(arguments[0], null).getPropertyValue('padding-right');", element).toString();
            String paddingTop = jsExecutor.executeScript("return window.getComputedStyle(arguments[0], null).getPropertyValue('padding-top');", element).toString();
            String paddingBottom = jsExecutor.executeScript("return window.getComputedStyle(arguments[0], null).getPropertyValue('padding-bottom');", element).toString();
            String marginLeft = jsExecutor.executeScript("return window.getComputedStyle(arguments[0], null).getPropertyValue('margin-left');", element).toString();
            String marginRight = jsExecutor.executeScript("return window.getComputedStyle(arguments[0], null).getPropertyValue('margin-right');", element).toString();
            String marginTop = jsExecutor.executeScript("return window.getComputedStyle(arguments[0], null).getPropertyValue('margin-top');", element).toString();
            String marginBottom = jsExecutor.executeScript("return window.getComputedStyle(arguments[0], null).getPropertyValue('margin-bottom');", element).toString();

            // Print the padding and margin values
            System.out.println("Padding:");
            System.out.println("Left: " + paddingLeft);
            System.out.println("Right: " + paddingRight);
            System.out.println("Top: " + paddingTop);
            System.out.println("Bottom: " + paddingBottom);
            System.out.println("Margin:");
            System.out.println("Left: " + marginLeft);
            System.out.println("Right: " + marginRight);
            System.out.println("Top: " + marginTop);
            System.out.println("Bottom: " + marginBottom);
        }

}