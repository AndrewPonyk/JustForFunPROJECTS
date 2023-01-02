package packtSaver.video;

import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import packtSaver.book.PacktDriver;

import java.io.File;
import java.util.*;

import static packtSaver.video.PacktVideoDriver.PACKT_VIDEO_DOWNLOAD_PATH;

public class PacktVideoDriverBack extends PacktDriver {

    private List<RemoteWebDriver> oldDrivers = new ArrayList<>();

    public void saveVideo(String url) {
        if (url != null) {
            packtWebDriver.get(url);
            sleep(2000);
        }

        String courseName = "undefined";
        try {
            courseName = packtWebDriver.findElement(By.cssSelector(".book-title")).getText();
        } catch (Exception e) {
            System.err.println("cant retrieve course name");
            throw e;
        }

        Map<String, List<Pair<String, String>>> sectionUrls = getSectionUrls();
        System.out.println("----");
        System.out.println(sectionUrls);
        System.out.println("-----");
        saveFiles(courseName, sectionUrls);
    }

    private void saveFiles(String courseName, Map<String, List<Pair<String, String>>> sectionUrls) {
        String folder = PACKT_VIDEO_DOWNLOAD_PATH + courseName + "\\";
        new File(folder).mkdir();
        packtWebDriver.quit();

        sectionUrls.keySet().forEach(item -> {
            String sectionFolder = folder + item.replaceAll("\n", "").replaceAll("/", "-");
            System.out.println(sectionFolder);
            new File(sectionFolder).mkdir();

            ChromeOptions options = new ChromeOptions();
            //options.addArguments("--load-extension=C:\\Users\\Andrii_Ponyk\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Extensions\\aiimdkdngfcipjohbjenkahhlhccpdbc\\31.2.5_0");
            HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
            options.addArguments("--user-data-dir=D:\\selenum\\ChromeProfile");
            chromePrefs.put("download.prompt_for_download", false);
            chromePrefs.put("download.default_directory", sectionFolder);
            options.setExperimentalOption("prefs", chromePrefs);

            oldDrivers.add(packtWebDriver);
            packtWebDriver = new ChromeDriver(options);

            //SKIP login in V2
//            try{
//                login(true);
//            }catch (Exception e){
//                System.out.println("try to login 2nd time");
//                packtWebDriver.quit();
//                packtWebDriver = new ChromeDriver(options);
//                login(true);
//            }
            ArrayList<String> tabs0 = new ArrayList<>(packtWebDriver.getWindowHandles());
            packtWebDriver.switchTo().window(tabs0.get(0));
            packtWebDriver.get("https://www.packtpub.com/");

            ArrayList<String> tabs = new ArrayList<>(packtWebDriver.getWindowHandles());
            if (tabs.size() < 2) {
                packtWebDriver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "t");
            }
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
                } catch (Exception e) {
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
                } catch (Exception e) {
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
        oldDrivers.forEach(driver -> {
            try {
                driver.quit();
            } catch (Exception e) {

            }
        });
    }

    private Map<String, List<Pair<String, String>>> getSectionUrls() {
        Map<String, List<Pair<String, String>>> result = new LinkedHashMap<>();
        //List<WebElement> dropdown = packtWebDriver.findElements(By.className("accordion"));
        WebElement tocParentElement = packtWebDriver.findElements(By.cssSelector(".toc-container")).get(0);

        //open all sections
        List<WebElement> sectionsElements = tocParentElement.findElements(By.cssSelector(".accordion"));
        sectionsElements.stream().forEach(element -> {
            try {
                //System.out.println("||||"+element.getAttribute("outerHTML")+"||||");
                if (!element.getAttribute("class").contains("colla") && !element.getAttribute("innerHTML").contains("collapsed")) {
                    element.click();
                    Thread.sleep(300);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        sectionsElements.forEach(section -> {
            final List<Pair<String, String>> sectionVideos = new ArrayList<>();
            String sectionText = section.findElements(By.cssSelector(".card-header")).get(0).getText();
            sectionText = sectionText.replaceAll("\n", "");
            section.findElements(By.cssSelector("li a")).forEach(e -> {
                Pair<String, String> linkPair = Pair.of(e.getText(), e.getAttribute("href"));
                sectionVideos.add(linkPair);
            });

            result.put(sectionText, sectionVideos);
        });

        return result;
    }
}