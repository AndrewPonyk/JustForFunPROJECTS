package dj;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.StringUtil;
import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static dj.DJBrowser.*;

public class VacRetriever {
    public static void main(String[] args) throws InterruptedException {

        String url = "https://djinni.co/jobs/?all-keywords=&any-of-keywords=&exclude-keywords=&primary_keyword=Java&page=";
        System.out.println("Retrive vacancies (default java) : \n" + url + "\n in case of custom url provide it and click enter" );

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if(!StringUtils.isEmpty(input)){
           url = input;
        }

        driver.get(url);

        driver.manage().window().maximize();

        // Zoom out to 49%
        int zoomLevel = 56;
        String jsScriptZoomOut = "document.body.style.zoom='" + zoomLevel + "%'; window.scrollBy(0, 50);";
        ((JavascriptExecutor) driver).executeScript(jsScriptZoomOut);

        List<String> vacUrls = new ArrayList<>();
        List<WebElement> pages = driver.findElements(By.cssSelector("li.page-item"));
        int totalpages = Integer.parseInt(pages.get(pages.size() - 2).getText()); // not minus 1 because last element is >> arrow, so last page is size-2
        for (int i = 1; i <= 1; i++) {
            driver.get(url + i);
            List<WebElement> vacs = driver.findElements(By.cssSelector("a.profile"));
            vacUrls.addAll(vacs.stream().map(v -> v.getAttribute("href")).collect(Collectors.toList()));
        }

        //System.out.println(vacUrls);
        int couner = 1;

        for (int i = 0; i < vacUrls.size(); i++) {
            driver.get(vacUrls.get(i));
            ((JavascriptExecutor) driver).executeScript(jsScriptZoomOut);
            Thread.sleep(20);

            WebElement vac = driver.findElement(By.cssSelector("body > div.wrapper > div.container.job-post-page > div:nth-child(2)"));
            String title = driver.findElement(By.cssSelector("h1")).getText().replace("\\", "-").replace("/", "-");
            System.out.println(couner
                    + "\n" + title
                    + "\n" + vac.getText());
            System.err.println("\n\n=========================================================================================");

            persistImage(zoomLevel, couner, vac, title);
            couner++;
        }


        driver.quit();
    }

    public static void persistImage(int zoomLevel, int couner, WebElement vac, String title) {
        //storing also image:
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            // Read the entire page screenshot as an image
            BufferedImage fullImg = ImageIO.read(screenshotFile);

            // Get the location and dimensions of the element
            int elementX = (int) (vac.getLocation().getX() * (1-Double.valueOf(zoomLevel)/100));
            int elementY = (int) (vac.getLocation().getY() * (1-Double.valueOf(zoomLevel)/100));
            int elementWidth = (int) (vac.getSize().getWidth()  * (1-Double.valueOf(zoomLevel)/100));
            int elementHeight = (int) (vac.getSize().getHeight() * (1-Double.valueOf(zoomLevel)/100));

            //TODO: elementX is almost correct but cant find it so adjust by 150
            elementX += 210;
            elementWidth+= 245;
            if(elementHeight < fullImg.getHeight()-200){
                elementHeight+=160;
            }


            // Crop the full image to get the element screenshot
            //will use 15 for elementY - because i know its on the top (just header has 15)
            BufferedImage elementScreenshot = fullImg.getSubimage(elementX, 15 /* elementY*/, elementWidth, (elementHeight + elementY) >= fullImg.getHeight() ? fullImg.getHeight() - elementY : elementHeight  );

            File elementScreenshotFile = new File("c:\\tmp\\djinni\\" + couner + "" + title + ".png");
            ImageIO.write(elementScreenshot, "png", elementScreenshotFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
