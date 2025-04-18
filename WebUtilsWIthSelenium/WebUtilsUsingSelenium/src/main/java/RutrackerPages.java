import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;


public class RutrackerPages {
	public static String[] stopEquals = {"Программирование" , "Web-дизайн", "Как качать с pluralsight"};
	public static String projectDir = System.getProperty("user.dir");
	public static String torrentFilesDir = projectDir + "/downloadedTorrents/";
	public static String dbFile = projectDir + "/rutracker.db";
	public static String downloadPrefix = "http://dl.rutracker.org/forum/dl.php?t=";

	public static void main(String[] args) {
		List<TorrentItem> programmingVideos = retrieveAndPrintTorrents("http://rutracker.org/forum/viewforum.php?f=1565&start=", 150); // Programming videos
		persistTorrentsList(programmingVideos, "ProgrammingVideos");

		//List<TorrentItem> webDesignVideos = retrieveAndPrintTorrents("http://rutracker.org/forum/viewforum.php?f=1564&start=", 1000); // Web Design videos
		//persistTorrentsList(webDesignVideos, "WebDesignVideos");

		//List<TorrentItem> webDesignVideos = retrieveAndPrintTorrents("http://rutracker.org/forum/viewforum.php?f=1426&start=", 3200); // Programming books
		//persistTorrentsList(webDesignVideos, "ProgrammingBooks");

		//

		//retrieveAndPrintTorrents("http://rutracker.org/forum/viewforum.php?f=1425&start=", 1950); // Web design and programming books

		// ---------------------------- Conditional searches
		//retrieveAndPrintTorrents("http://rutracker.org/forum/viewforum.php?f=1426&start=", 3200, " r "); // R books
		//retrieveAndPrintTorrents("http://rutracker.org/forum/viewforum.php?f=1426&start=", 3200, "assembly"); // Assembly books
		//retrieveAndPrintTorrents("http://rutracker.org/forum/viewforum.php?f=1426&start=", 3200, "machine", "learning"); // Artificial books
		//retrieveAndPrintTorrents("http://rutracker.org/forum/viewforum.php?f=1426&start=", 3200, "hibernate"); // Hibernate books
		//retrieveAndPrintTorrents("http://rutracker.org/forum/viewforum.php?f=1565&start=", 2900, "c#"); // Programming videos


		//---------------------------------------------------------------
	}

	public static void persistTorrentsList(List<TorrentItem> items, String tableName){
		Connection c = null;
		Statement stmt = null;
		List<String> existingTorrents = new ArrayList<String>();
		int addedTorrents = 0;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
			stmt = c.createStatement();

			String selectAllSql = "select * from " + tableName;
			ResultSet all = stmt.executeQuery(selectAllSql);
			while(all.next()){
				existingTorrents.add(all.getString("title"));
			}

			String sql = String.format("insert into %s(title, size, flag, other, torrent_file) values ", tableName);
			for(TorrentItem item : items){
				if(!startsInList(existingTorrents, item.title)){
					sql += String.format(" ('%s', '%s', %s, '%s', '%s'),",
							item.title, item.size, item.flag, item.other, item.torrent_file);
					addedTorrents++;
				}
			}

			sql = sql.substring(0, sql.length()-1);
			System.out.println(sql);
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Added " + addedTorrents + " Torrents");
	}

	public static List<TorrentItem> retrieveFromDb(String tableName) {
		Connection c = null;
		Statement stmt = null;
		List<TorrentItem> resultList = new ArrayList<TorrentItem>();

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbFile);

			stmt = c.createStatement();
			String sql = "select * from " + tableName;

			ResultSet result = stmt.executeQuery(sql);
			while (result.next()) {
				resultList.add(new TorrentItem().setId(result.getInt("id")).setTitle(result.getString("title")).
						setSize(result.getString("size")).setFlag(result.getInt("flag")).setOther(result.getString("other")));
/*				System.out.println(String.format("%s \t %s \t\t\t\t\t\t %s \t %s \t %s",
						result.getInt("id"), result.getString("title"),
						result.getString("size"), result.getInt("flag"), result.getString("other")));*/
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	public static List<TorrentItem> retrieveAndPrintTorrents(String url, int count, String ... containsConditions){
		System.out.println("Rutracker pages retriewer");


		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("browser.download.folderList", "2");
		profile.setPreference("browser.download.manager.showWhenStarting",false);
		profile.setPreference("browser.download.dir","/home/andrew/git/JustForFunPROJECTS/WebUtilsWIthSelenium/WebUtilsUsingSelenium/downloadedTorrents/");

		//WebDriver driver = new HtmlUnitDriver();
		WebDriver driver = new FirefoxDriver();
		//WebDriver driver2ForDownload = new FirefoxDriver(profile);
		WebDriver driver2ForDownload = new FirefoxDriver();////after change to<artifactId>selenium-java</artifactId> to 3.141.59, FROM 3.4.0
		int page = 1, counter = 1, offset = 0;
		List<String> items = new ArrayList<String>();
		List<TorrentItem> result = new ArrayList<TorrentItem>();


		driver.get("http://rutracker.org/forum/index.php");
		driver.findElement(By.xpath("//*[@id=\"page_header\"]/div[4]/table/tbody/tr/td/span[1]")).click();
		driver.findElement(By.id("top-login-uname")).sendKeys("andrewlviv");
		driver.findElement(By.id("top-login-pwd")).sendKeys("ekzyrf");
		driver.findElement(By.id("top-login-btn")).click();

		driver2ForDownload.get("http://rutracker.org/forum/index.php");
		driver2ForDownload.findElement(By.xpath("//*[@id=\"page_header\"]/div[4]/table/tbody/tr/td/span[1]")).click();
		driver2ForDownload.findElement(By.id("top-login-uname")).sendKeys("andrewlviv");
		driver2ForDownload.findElement(By.id("top-login-pwd")).sendKeys("ekzyrf");
		driver2ForDownload.findElement(By.id("top-login-btn")).click();
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		while(offset <= count){
			if(containsConditions.length ==0){
				System.out.println("============== Page " + page + " ===================+++++============");
			}

			driver.get(url+offset);
			List<WebElement> rows = driver.findElements(By.tagName("tr"));
			for(WebElement elem : rows){
				try{
					//String title = elem.findElements(By.cssSelector("a")).get(0).getText();
					//String size = elem.findElements(By.cssSelector("td")).get(2).getText();

					// WHEN LOGIN
					String title = elem.findElements(By.cssSelector("a.tt-text")).get(0).getText();
					String torrentHref = elem.findElements(By.cssSelector("a.tt-text")).get(0).getAttribute("href");
					String size = elem.findElements(By.cssSelector("td")).get(2).findElements(By.cssSelector("div div")).get(2).getText();

						boolean print = true;

						for(String stopWord : stopEquals){
							if(title.equals(stopWord)){
								print = false;
							}
						}
						for(String condition : containsConditions){
							if(!title.toLowerCase().contains(condition)){
								print = false;
							}
						}
					if (print) {
						System.out.println(torrentHref + "<<<<<---<");
						String torrentResFile =  RutrackerPages.torrentFilesDir +  torrentHref.substring(torrentHref.indexOf("t=")+2) + ".torrent";
						System.out.println(torrentResFile + ": " + torrentHref);
						driver2ForDownload.get(torrentHref);

						System.out.println("00000000000000000000000000");

						driver2ForDownload.findElement(By.cssSelector(".dl-stub.dl-link")).click();

							title = title.replaceAll("'", "\"");
							TorrentItem item = new TorrentItem().setId(counter).setTitle(title).setSize(size).setFlag(0).setOther("").setTorrentFile(torrentHref);
							if(items.contains(title)){
								item.setOther("  -->DUPLICATE +(# " + items.indexOf(title)+ ")");
							}
							items.add(title);
							result.add(item);
							System.out.println(counter + ") " +  title + "      " + "[" + size + "]");
							counter++;
						}
				}catch(Exception e){
					System.err.println(e.getMessage());
				}
			}

			offset+= 50;
			page++;
		}
		return result;
	}

	public static boolean startsInList(List<String> items, String s){
		for(String item : items){
			if(item.toLowerCase().contains(s.toLowerCase())){
				return true;
			}
		}
		return false;
	}
}

class TorrentItem{
	public int id;
	public String title;
	public String size;
	public int flag;
	public String other;
	public String torrent_file;

	public TorrentItem setId(int id){
		this.id = id;
		return this;
	}

	public TorrentItem setTitle(String title){
		this.title = title;
		return this;
	}

	public TorrentItem setSize(String size){
		this.size = size;
		return this;
	}

	public TorrentItem setFlag(int flag){
		this.flag = flag;
		return this;
	}

	public TorrentItem setOther(String other){
		this.other = other;
		return this;
	}

	public TorrentItem setTorrentFile(String s){
		this.torrent_file = s;
		return  this;
	}


}