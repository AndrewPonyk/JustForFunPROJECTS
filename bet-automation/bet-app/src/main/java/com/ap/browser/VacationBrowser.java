package com.ap.browser;

import com.ap.dao.BetRepo;
import com.ap.dao.BetRepoJdbc;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class VacationBrowser {
    public static FirefoxDriver driver;
    public static WebDriverWait wait;

    static {
        System.setProperty("webdriver.gecko.driver", "/home/andrii/Programs/geckodriver");
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");

        driver = new FirefoxDriver();
        wait = new WebDriverWait(driver, 9);
    }

    public void startVacationBrowser(){
        for (String item : SEARCH_URLS){
            driver.get(item);
            Thread.sleep(25000);
        }

        driver.quit();
    };

    public static final String[] SEARCH_URLS = {
            "http://www.lembergtravel.com/poshuk-turiv.html#!page=form&f=5&t=%D0%A2%D1%83%D0%BD%D1%96%D1%81&i=114&y=country&c=2018-07-25&v=2018-07-31&l=8&p=2&tc=&n=0&g=1&d=1397&o=uai%2Cai&st=5&pf=500&pt=100000&rt=0%2C10&e=&sr=&r=air%2Cbus%2Ctrain%2Cship&w=&ex=1",
            "http://www.lembergtravel.com/poshuk-turiv.html#!f=5&t=%D0%84%D0%B3%D0%B8%D0%BF%D0%B5%D1%82&i=43&y=country&c=2018-07-25&v=2018-07-31&l=8&p=2&tc=&n=0&g=1&d=1397&o=uai%2Cai&st=5&pf=500&pt=100000&rt=0%2C10&e=one_line_beach&sr=&r=air%2Cbus%2Ctrain%2Cship&w=&ex=1&page=form",
            "http://www.lembergtravel.com/poshuk-turiv.html#!f=5&t=%D0%9A%D1%96%D0%BF%D1%80&i=54&y=country&c=2018-07-25&v=2018-07-31&l=8&p=2&tc=&n=0&g=1&d=1397&o=uai%2Cai%2Cfb%2Chb&st=4&pf=500&pt=100000&rt=0%2C10&e=&sr=&r=air%2Cbus%2Ctrain%2Cship&w=&ex=1&page=form",
            "http://www.lembergtravel.com/poshuk-turiv.html#!f=5&t=%D0%86%D1%81%D0%BF%D0%B0%D0%BD%D1%96%D1%8F&i=49&y=country&c=2018-07-25&v=2018-07-31&l=8&p=2&tc=&n=0&g=1&d=1397&o=uai%2Cai%2Cfb%2Chb&st=4&pf=500&pt=100000&rt=0%2C10&e=&sr=&r=air%2Cbus%2Ctrain%2Cship&w=&ex=1&page=form"
    };

}
