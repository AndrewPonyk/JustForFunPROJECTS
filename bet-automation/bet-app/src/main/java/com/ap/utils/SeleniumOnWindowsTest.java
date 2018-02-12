package com.ap.utils;

import org.openqa.selenium.firefox.FirefoxDriver;

public class SeleniumOnWindowsTest {
	public static void main(String[] args) {
		System.out.println("Test selenium on windows");
	    FirefoxDriver driver;
        System.setProperty("webdriver.gecko.driver", "C:\\Programs\\geckodriver18.exe");

        driver = new FirefoxDriver();
        driver.get("http://google.com.ua");
	}
}
