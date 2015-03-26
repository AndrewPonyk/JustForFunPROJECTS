package packtbookslist;

import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class PacktBooksListProgram {

	public static void main(String[] args) {
		//new PacktBooksListProgram().runApp();
		System.out.println(new Date().toString());
	}

	public void runApp() {
		System.out.println("Listing of all packt books");
		int bookCounter = 1;
		int offsetCounter = 0;
		String url = "https://www.packtpub.com/all?search=&type_list%5Bbooks%5D=books&offset= + "
				+ 0 + "&rows=48&sort=ss_cck_field_date_of_publication+desc";

		WebDriver driver = new FirefoxDriver();

		for (int i = 1; i <= 57; i++) {

			System.out.println("Page " + i
					+ "===============================================");
			url = "https://www.packtpub.com/all?search=&type_list%5Bbooks%5D=books&offset="
					+ offsetCounter
					+ "&rows=48&sort=ss_cck_field_date_of_publication+desc";
			driver.get(url);
			System.out.println(url);
			List<WebElement> books = driver
					.findElements(By
							.cssSelector(".book-block-outer .book-block-outer .book-block-title"));

			for (WebElement item : books) {
				System.out.println(bookCounter + ") " + item.getText());
				bookCounter++;
			}
			offsetCounter += 48;
		}
	}
}
