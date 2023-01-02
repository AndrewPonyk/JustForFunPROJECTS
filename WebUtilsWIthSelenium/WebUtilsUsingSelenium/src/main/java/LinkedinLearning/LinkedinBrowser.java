
//!!!!!!!!!!!!!
//https://stackoverflow.com/questions/8344776/can-selenium-interact-with-an-existing-browser-session

package LinkedinLearning;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static packtbookslist.PacktBooksListProgram.driver;

//"https://www.linkedin.com/learning/topics/cloud-computing-5?u=2113185"
//"https://www.linkedin.com/learning/topics/data-science?u=2113185 data science
//"https://www.linkedin.com/learning/topics/database-management?u=2113185"
//"https://www.linkedin.com/learning/topics/devops?u=2113185"
//"https://www.linkedin.com/learning/topics/technology?u=2113185" it helpdesk
//"https://www.linkedin.com/learning/topics/mobile-development?u=2113185"
//"https://www.linkedin.com/learning/topics/network-and-system-administration?u=2113185" network/system administration
//"https://www.linkedin.com/learning/topics/security-3?u=2113185" SECURITY
//"https://www.linkedin.com/learning/topics/software-development?u=2113185"
//"https://www.linkedin.com/learning/topics/technology?u=2113185" web development

public class LinkedinBrowser {
    protected static RemoteWebDriver driver;

    static List<String> urls = List.of(
            "https://www.linkedin.com/learning/topics/technology?sortBy=RECENCY&u=2113185", /*TECHNOLOGY GLOBAL TOPIC*/
            "https://www.linkedin.com/learning/topics/cloud-computing-5?sortBy=RECENCY&u=2113185",
            "https://www.linkedin.com/learning/topics/data-science?sortBy=RECENCY&u=2113185",
            "https://www.linkedin.com/learning/topics/database-management?sortBy=RECENCY&u=2113185", /*Database Management*/
            "https://www.linkedin.com/learning/topics/devops?sortBy=RECENCY&u=2113185", /*DevOps*/
            "https://www.linkedin.com/learning/topics/mobile-development?u=2113185",
            "https://www.linkedin.com/learning/topics/network-and-system-administration?u=2113185",
            "https://www.linkedin.com/learning/topics/security-3?u=2113185",
            "https://www.linkedin.com/learning/topics/software-development?u=2113185",
            "https://www.linkedin.com/learning/topics/technology?u=2113185"
    );

    static {
        System.setProperty("webdriver.chrome.driver", driver());
        ChromeOptions options = new ChromeOptions();

        //options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        options.addArguments("--user-data-dir=D:\\selenum\\ChromeProfile");
        //WORKSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS nice
        //started chrome for first time with command:
        //C:\Users\hp>start chrome.exe --remote-debugging-port=9222 --user-data-dir="D:\selenum\ChromeProfile"

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        options.merge(capabilities);

        driver = new ChromeDriver(options);
        System.out.println(driver);
    }

    public static void retrieveNamesAndCoursesInfo() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        driver.get(urls.get(0));
        driver.manage().window().maximize();
        Scanner readinput = new Scanner(System.in);

        for (String url : urls) {
            driver.get(url);
            final WebElement topicTitle = driver.findElementByCssSelector(".topics-body__title");
            final WebElement topicDescription = driver.findElementByCssSelector(".topics-body__description");
            System.out.println("*****************************************************************");
            System.out.println("====                                                            ===");
            System.out.println(topicTitle.getText());
            System.out.println(topicDescription.getText());

            while (true) {
                final List<WebElement> buttons = driver.findElementsByCssSelector("button.finite-scroll__load-button");
                 Optional<WebElement> show_more = Optional.empty();
                        try{
                            System.out.println("buttons count: " + buttons.size());
                            show_more         = buttons.stream().filter(b -> b.getText() != null && b.getText().equals("Show more")).findFirst();
                        }catch (Exception e){}


                js.executeScript("window.scrollBy(0,document.body.scrollHeight)");
                Thread.sleep(800);
                if (!show_more.isPresent() && false) {
                    break;
                }
                try {
                    show_more.get().click();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }


            }
            System.out.println("end section. Press enter");
            readinput.nextLine();
        }
    }

}
