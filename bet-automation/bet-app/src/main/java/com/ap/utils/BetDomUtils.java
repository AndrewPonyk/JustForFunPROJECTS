package com.ap.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

public class BetDomUtils {

    public static void setAttribute(WebDriver driver, WebElement element, String attName, String attValue) {
        ((RemoteWebDriver)driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);",
                element, attName, attValue);
    }
}
