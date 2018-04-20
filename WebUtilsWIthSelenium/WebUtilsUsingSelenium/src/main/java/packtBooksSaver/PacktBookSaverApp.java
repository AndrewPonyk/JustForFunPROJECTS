package packtBooksSaver;

import org.openqa.selenium.By;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PacktBookSaverApp {
    public static void main(String[] args) throws FileNotFoundException {
        PacktDriver driver = new PacktDriver();
        driver.login();

        driver.saveBook("https://www.packtpub.com/mapt/book/big_data_and_business_intelligence/9781785889622");
        driver.convertImagesToPdf();
 //       saveBooks("/home/andrii/git/JustForFunPROJECTS/WebUtilsWIthSelenium/WebUtilsUsingSelenium/packttemp.txt");
    }

    public static void saveBooks(String fileName) throws FileNotFoundException {
        System.out.println("Saving all packt books:");
        try (Scanner scanner = new Scanner(new FileInputStream(fileName))) {

            PacktDriver driver = new PacktDriver();
            driver.login();

            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.contains("[Video]") || !line.contains("::")) {
                    continue;
                }

                String url = line.substring(line.indexOf("::") + 2);
                driver.getMaptUrlAndSaveBook(url);
                System.out.println(url);
            }
        }

    }
}
