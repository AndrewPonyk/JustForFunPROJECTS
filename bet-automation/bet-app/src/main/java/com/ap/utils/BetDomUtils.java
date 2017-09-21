package com.ap.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

public class BetDomUtils {

    public static void setAttribute(RemoteWebDriver driver, WebElement element, String attName, String attValue) {
        driver.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);",
                element, attName, attValue);
    }
}
