package botsfinder.porohs;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.util.HashMap;

public class PorohsBrowser {
    protected RemoteWebDriver packtWebDriver;
    public PorohsBrowser() {
        System.setProperty("webdriver.gecko.driver", "/home/andrii/Programs/geckodriver");
        System.setProperty("webdriver.chrome.driver", driver());

        //
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--load-extension=C:\\Users\\Andrii_Ponyk\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Extensions\\aiimdkdngfcipjohbjenkahhlhccpdbc\\31.2.5_0");

        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("download.prompt_for_download", false);
        chromePrefs.put("download.default_directory", "C:\\tmp\\packt\\video\\");
        options.setExperimentalOption("prefs", chromePrefs);

        packtWebDriver = new ChromeDriver(options);
    }

    public static String driver(){
        File driver = new File("C:\\tmp\\chromedriver.exe");
        if(driver.exists()){
            return "C:\\tmp\\chromedriver.exe";
        } else {
            return "/home/andrii/Programs/chromedriver";
        }
    }
}
