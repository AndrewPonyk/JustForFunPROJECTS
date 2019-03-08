package scanlibsdownload;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScanlibsDownloadBrowser {

    protected static RemoteWebDriver scanlibsWebDriver;

    private static String tempDir = System.getProperty("user.home") + "/scanlibs_downloads/";
    static {
        System.setProperty("webdriver.chrome.driver", driver());

        scanlibsWebDriver = new ChromeDriver();
    }

    public static String download(String url){
        loginTurbobit();

        List<List<ScanlibsItem>> scanlibsItems = getDownloadableItems(url);
        System.out.println(scanlibsItems);

        return performDownload(scanlibsItems);
    }

    private static void loginTurbobit() {
        scanlibsWebDriver.get("https://turbobit.net/");

        //captcha !!!!
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static String performDownload(List<List<ScanlibsItem>> scanlibsItems) {
        scanlibsItems.forEach(page->{
            page.forEach(item->{
                String itemHandle = scanlibsWebDriver.getWindowHandle();
                scanlibsWebDriver.get(item.getItemUrl());
                List<WebElement> links = scanlibsWebDriver.findElements(By.cssSelector("div[align='center'] a"));
                links.forEach(link->{

                    if(fileExists(link.getText())){
                        System.out.println("Fixe exists->>"+fileExists(link.getText()) + link.getText());
                        return;
                    }
                    link.click();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Set<String> windowHandles = scanlibsWebDriver.getWindowHandles();
                    windowHandles.forEach(windowHandle->{
                        scanlibsWebDriver.switchTo().window(windowHandle);
                        if (!scanlibsWebDriver.getCurrentUrl().startsWith("https://scan") &&
                                !scanlibsWebDriver.getCurrentUrl().contains("turbo")) {
                            scanlibsWebDriver.executeScript("window.close()");
                            scanlibsWebDriver.switchTo().window(itemHandle);
                        }
                        if(scanlibsWebDriver.getPageSource().toLowerCase().contains("oops! that page")){
                            scanlibsWebDriver.executeScript("window.close()");
                            scanlibsWebDriver.switchTo().window(itemHandle);
                        }
                        if(scanlibsWebDriver.getCurrentUrl().contains("turbo")){
                            //need to check if element is present
                            //skip in case of not found
                            if(scanlibsWebDriver.getPageSource().toLowerCase().contains("file was not found")||
                            scanlibsWebDriver.getPageSource().toLowerCase().contains("searching for")){
                                scanlibsWebDriver.executeScript("window.close()");
                                scanlibsWebDriver.switchTo().window(itemHandle);
                            } else{
                                scanlibsWebDriver.findElement(By.cssSelector(".premium-link-block a")).click();
                                boolean progressDownload = isProgressDownload();
                                while (progressDownload){
                                    System.out.println("download in progress");
                                    progressDownload = isProgressDownload();
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                            scanlibsWebDriver.executeScript("window.close()");
                            scanlibsWebDriver.switchTo().window(itemHandle);

                        }
                    });

                });
            });
        });

        return "";
    }

    public static boolean isProgressDownload(){
        File folder = new File("/home/pihura_olia/Downloads");
        File[] files = folder.listFiles();
        Boolean result  = false;

        for (int i =0;i<files.length;i++){
            //System.out.println(files[i].getName());
            if(files[i].getName().matches(".*crd.*")){
                result = true;
            }
        }

        return result;
    }

    public static boolean fileExists(String linkText){
        File folder = new File("/home/pihura_olia/Downloads");
        File[] files = folder.listFiles();
        Boolean result  = false;

        linkText = linkText.replaceAll("[^A-Za-z0123456789]","");
        for (int i =0;i<files.length;i++){

            if(files[i].getName().replaceAll("[^A-Za-z0123456789]","").startsWith(linkText)){
                System.out.println(files[i].getName());
                result = true;
            }
        }

        return result;
    }

    private static List<List<ScanlibsItem>> getDownloadableItems(String url) {
        scanlibsWebDriver.get(String.format(url, "1"));
        List<List<ScanlibsItem>> scanlibsItems = new ArrayList<>();

        List<WebElement> pagenumbers =
                scanlibsWebDriver.findElements(By.cssSelector("[class=page-numbers]"));
        int totalPages = Integer.parseInt(pagenumbers.get(pagenumbers.size()-1).getText());
        System.out.println("Total pages:" + totalPages);

        for (int i = 1; i<=15; i++){
            List<ScanlibsItem> pageItems = new ArrayList<>();
            scanlibsWebDriver.get(String.format(url, i+""));

            List<WebElement> entries = scanlibsWebDriver.findElements(By.className("post-inner-content"));
            entries.forEach(entry->{
                pageItems.add(new ScanlibsItem(entry.findElement(By.className("entry-title")).getText(),
                        "",
                        entry.findElement(By.className("read-more")).getAttribute("href")));
            });


            scanlibsItems.add(pageItems);
        }
        return scanlibsItems;
    }

    public static String driver(){
        File driver = new File("C:\\tmp\\chromedriver.exe");
        if(driver.exists()){
            return "C:\\tmp\\chromedriver.exe";
        } else {
            return System.getProperty("user.home") +"/Programs/chromedriver";
        }
    }
}
