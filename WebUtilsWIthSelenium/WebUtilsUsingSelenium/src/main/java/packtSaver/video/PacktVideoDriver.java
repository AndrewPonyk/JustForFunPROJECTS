package packtSaver.video;

import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import packtSaver.book.PacktDriver;

import java.io.File;
import java.util.*;

public class PacktVideoDriver extends PacktDriver {

    private List<RemoteWebDriver> oldDrivers = new ArrayList<>();

    public void saveVideo(String url) {
        if (url != null) {
            packtWebDriver.get(url);
            sleep(4000);
        }

        String courseName = "undefined";
        try {
            courseName = packtWebDriver.findElement(By.cssSelector("h3.mt0")).getText();
        }catch (Exception e){

        }
        Map<String, List<Pair<String, String>>> sectionUrls = getSectionUrls();
        System.out.println("----");
        System.out.println(sectionUrls);
        System.out.println("-----");
        saveFiles(courseName, sectionUrls);
    }

    private void saveFiles(String courseName, Map<String, List<Pair<String, String>>> sectionUrls) {
        String folder = "C:\\tmp\\packt\\video\\" + courseName+"\\";
        new File(folder).mkdir();
        packtWebDriver.quit();

        sectionUrls.keySet().forEach(item -> {
            String sectionFolder = folder + item.replaceAll("\n", "").replaceAll("/", "-");
            System.out.println(sectionFolder);
            new File(sectionFolder).mkdir();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--load-extension=C:\\Users\\Andrii_Ponyk\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Extensions\\aiimdkdngfcipjohbjenkahhlhccpdbc\\31.2.5_0");
            HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
            chromePrefs.put("download.prompt_for_download", false);
            chromePrefs.put("download.default_directory", sectionFolder);
            options.setExperimentalOption("prefs", chromePrefs);

            oldDrivers.add(packtWebDriver);
            packtWebDriver = new ChromeDriver(options);

            try{
                login(true);
            }catch (Exception e){
                System.out.println("try to login 2nd time");
                packtWebDriver.quit();
                packtWebDriver = new ChromeDriver(options);
                login(true);
            }

            List<String> tabs = new ArrayList<>(packtWebDriver.getWindowHandles());
            packtWebDriver.switchTo().window(tabs.get(1));
            packtWebDriver.get("chrome-extension://aiimdkdngfcipjohbjenkahhlhccpdbc/popup.html");

            sectionUrls.get(item).forEach((Pair<String, String> link) -> {
                packtWebDriver.switchTo().window(tabs.get(0));
                packtWebDriver.get(link.getRight());
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    WebElement playButton = packtWebDriver.findElements(By.cssSelector(".jw-video.jw-reset")).get(0);
                    playButton.click();
                }catch (Exception e){
                    //can not stop video playback
                    System.out.println("FAIL TO STOP VIDEO");
                }
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                packtWebDriver.switchTo().window(tabs.get(1));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    packtWebDriver.findElements(By.className("download_button")).get(0).click();
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    System.out.println("CAN NOT SAVE VIDEO " + link.getLeft());
                }

            });

        });
        //check if downloads are completed, and if so then EXIT
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        oldDrivers.forEach(driver->{
            try {
                driver.quit();
            } catch (Exception e){

            }
        });
    }

    private Map<String, List<Pair<String, String>>> getSectionUrls() {
        Map<String, List<Pair<String, String>>> result = new LinkedHashMap<>();
        List<WebElement> dropdown = packtWebDriver.findElements(By.className("cover-accordion"));
        WebElement tocParentElement = dropdown.get(0).findElements(By.cssSelector("#item-one")).get(0);

        //open all sections
        List<WebElement> sectionsElements = tocParentElement.findElements(By.cssSelector("div.cover-toc__title"));
        sectionsElements.stream().forEach(element -> {
            try {
                //System.out.println("||||"+element.getAttribute("outerHTML")+"||||");
                if (element.getAttribute("class").contains("colla")) {
                    element.click();
                    Thread.sleep(500);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        sectionsElements = tocParentElement.findElements(By.cssSelector("div.cover-toc__title"));

        sectionsElements.forEach(section -> {
            final List<Pair<String, String>> sectionVideos = new ArrayList<>();
            System.err.println(section.getText());
            WebElement parent = (WebElement) ((JavascriptExecutor) packtWebDriver)
                    .executeScript(
                            "return arguments[0].parentNode;", section);
            parent.findElements(By.cssSelector("li a")).forEach(e -> {
                Pair<String, String> linkPair = Pair.of(e.getText(), e.getAttribute("href"));
                sectionVideos.add(linkPair);
            });

            result.put(section.getText(), sectionVideos);
        });

        return result;
    }
}