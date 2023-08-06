package dj;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import static packtbookslist.PacktBooksListProgram.driver;

public class DJBrowser {
    protected static RemoteWebDriver driver;

    static {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-data-dir=C:\\tmp\\SELENIUM_DATA_FOLDER___TRY_TO_CLEAN\\");

        System.setProperty("webdriver.chrome.driver", driver());
        driver = new ChromeDriver(options);
    }
}