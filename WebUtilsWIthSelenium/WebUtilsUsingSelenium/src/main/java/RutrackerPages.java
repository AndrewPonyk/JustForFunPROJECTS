import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;


public class RutrackerPages {
	public static void main(String[] args) {
		System.out.println("Rutracker pages retriewer");

		WebDriver driver = new HtmlUnitDriver();
		int page = 1, counter = 1, offset = 0;

		while(offset <= 2900){
			String url = "http://rutracker.org/forum/viewforum.php?f=1565&start=" + offset;
			System.out.println("============== Page " + page + " ===================+++++============");

			driver.get(url);
			List<WebElement> rows = driver.findElements(By.tagName("tr"));
			for(WebElement elem : rows){
				try{
					WebElement title = elem.findElements(By.cssSelector("a")).get(0);
					WebElement size = elem.findElements(By.cssSelector("td")).get(2);

					if(!title.getText().equals("Программирование") && !title.getText().contains("Как качать с")){
						System.out.println(counter++ + ") " +  title.getText() + "      " + "[" + size.getText() + "]");
					}
				}catch(Exception e){
				}
			}

			offset+= 50;
			page++;
		}

	}
}
