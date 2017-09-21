package packtBooksSaver;

import com.itextpdf.text.*;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PacktDriver {
    FirefoxDriver packtWebDriver;
    private int pagesCounter;
    private String bookName;

    public PacktDriver() {
        System.setProperty("webdriver.gecko.driver", "/home/andrii/Programs/geckodriver");
        packtWebDriver = new FirefoxDriver();
    }

    public void login() {
        packtWebDriver.get("https://www.packtpub.com/");
        packtWebDriver.manage().window().maximize();
        List<WebElement> loginLink = packtWebDriver.findElements(By.cssSelector(".login-popup div"));
        System.out.println(loginLink);
        loginLink.get(0).click();

        List<WebElement> loginElements = packtWebDriver.findElements(By.cssSelector("#email"));
        List<WebElement> passwordElements = packtWebDriver.findElements(By.cssSelector("#password"));
        setAttribute(loginElements.get(1), "value", "andrew9999@ukr.net");
        setAttribute(passwordElements.get(1), "value", "Zz123456");
        packtWebDriver.findElements(By.cssSelector("#edit-submit-1")).get(1).click();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void saveBook(String url) {
        this.pagesCounter = 0;
        saveTOC(url);
        List<String> sectionsUrls = getSectionUrls();
        saveSectionsAsImages(sectionsUrls);
        convertImagesToPdf();
    }

    public void getMaptUrlAndSaveBook(String bookUrl) {
        packtWebDriver.get(bookUrl);
        //hack to close hover menu
        packtWebDriver.executeScript("window.scrollTo(0, arguments[0]);", 400);
        sleep(3000);
        try {
            System.out.println("::::" + packtWebDriver.findElement(By.cssSelector(".book-subscribed-read-inner input")));
            packtWebDriver.findElement(By.cssSelector(".book-subscribed-read-inner input")).click();
            saveBook(null);
        } catch (Exception e) {
            System.out.println("There is no book in MAPT" + bookUrl);
        }

    }

    public String convertImagesToPdf() {
        final Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("/home/andrii/Downloads/packt/" + this.bookName + ".pdf"));
            document.open();
            IntStream.range(0, this.pagesCounter).forEach(i -> {
                try {
                    Image image = Image.getInstance("/home/andrii/Downloads/packt/images/" + this.bookName + i + ".png");
                    //Setting zero, zero - cause some unpredictible location,
                    image.setAbsolutePosition(1, 1);
                    document.setPageSize(new Rectangle(image.getWidth(), image.getHeight()+2));
                    document.newPage();
                    document.add(image);
                    System.out.println("Adding " + i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveSectionsAsImages(List<String> sectionsUrls) {
        sectionsUrls.forEach(url -> {
            takeScreenshot(url);
        });
    }

    private List<String> getSectionUrls() {
        List<WebElement> dropdown = packtWebDriver.findElements(By.className("cover-accordion"));
        WebElement tocParentElement = dropdown.get(0).findElements(By.cssSelector("#item-one")).get(0);

        return tocParentElement.findElements(By.cssSelector("li a")).
                stream().
                map(element -> element.getAttribute("href")).
                collect(Collectors.toList());
    }

    private String saveTOC(String url) {
        if(url != null){
            packtWebDriver.get(url);
        }
        sleep(4000);
        this.bookName = packtWebDriver.findElement(By.cssSelector("h3")).getText();
        List<WebElement> dropdown = packtWebDriver.findElements(By.className("cover-accordion"));

        //open TOC
        dropdown.get(0).findElements(By.tagName("a")).get(0).click();

        //open all sections
        WebElement tocParentElement = dropdown.get(0).findElements(By.cssSelector("#item-one")).get(0);
        List<WebElement> sectionsElements = tocParentElement.findElements(By.cssSelector("div.cover-toc__title"));
        sectionsElements.forEach(element -> element.click());

        //open description
        dropdown.get(0).findElements(By.cssSelector(".list-group-item")).get(1).click();

        String tocFileName = takeScreenshot(null);
        return tocFileName;
    }

    public String takeScreenshot(String url) {
        if (url != null) {
            packtWebDriver.get(url);
            sleep(4500);
        }
        removeUnusedElements();
        expandScrollables();
        packtWebDriver.executeScript("window.scrollTo(0, arguments[0]);", 0);
        sleep(500);
        removeSideBar();
        sleep(1000);
        String js = "return Math.max( document.body.scrollHeight, document.body.offsetHeight,  document.documentElement.clientHeight,  document.documentElement.scrollHeight,  document.documentElement.offsetHeight);";
        Integer scrollheight = new Integer(packtWebDriver.executeScript(js).toString());

        List<BufferedImage> slices = new ArrayList<>();
        Integer offset = 0;
        List<Integer> offset_arr = new ArrayList<>();
        int off = 0;
        System.out.println("Scroll height:" + scrollheight);
        while (offset < (scrollheight - 260)) {
            if (offset > scrollheight) {
                packtWebDriver.executeScript("window.scrollTo(0, arguments[0]);", scrollheight);
            } else {
                packtWebDriver.executeScript("window.scrollTo(0, arguments[0]);", offset);
            }
            sleep(600);
            File file = packtWebDriver.getScreenshotAs(OutputType.FILE);

            BufferedImage img = null;
            try {
                img = ImageIO.read(file);
                slices.add(img);
                FileUtils.copyFile(file, new File("/home/andrii/Downloads/packt/images/" + this.bookName + this.pagesCounter++ + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            offset += img.getHeight();
        }

//        int x = 0, y = 0;
//        BufferedImage result = new BufferedImage(
//                slices.get(0).getWidth(), slices.get(0).getHeight() * slices.size(),
//                BufferedImage.TYPE_INT_RGB);
//        Graphics g = result.getGraphics();
//        for (int i = 0; i < slices.size(); i++) {
//            BufferedImage bi = slices.get(i);
//            g.drawImage(bi, x, y, null);
//            y += bi.getHeight();
//        }
//
//        url = packtWebDriver.getCurrentUrl();
//        String resultFilename = url.replaceAll("/", "|") + new Date().toString() + ".png";
//        try {
//            ImageIO.write(result, "png", new File("/home/andrii/Downloads/packt/" + resultFilename));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return "it is possible to merge images easy, but i would rather save them all";
    }

    public void removeSideBar() {
        //sidebar-wrapper
        try {
            packtWebDriver.findElementById("menu-toggle").click();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAttribute(WebElement element, String attName, String attValue) {
        packtWebDriver.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);",
                element, attName, attValue);
    }

    public WebElement fluentWait(final By locator) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(packtWebDriver)
                .withTimeout(10, TimeUnit.SECONDS)
                .pollingEvery(5, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);

        WebElement foo = wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(locator);
            }
        });

        return foo;
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void expandScrollables() {
        packtWebDriver.executeScript("[].forEach.call(document.querySelectorAll('pre'),function (el) {el.setAttribute('style','height:auto !important;overflow:visible !important;max-height:100em !important;');});");
    }

    private void removeUnusedElements() {
        try {
            packtWebDriver.executeScript("document.getElementById('recomendations').remove()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}