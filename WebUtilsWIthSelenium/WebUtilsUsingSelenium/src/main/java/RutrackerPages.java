import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;


public class RutrackerPages {
	public static String[] stopEquals = {"Программирование" , "Web-дизайн", "Как качать с pluralsight"};
	public static String projectDir = System.getProperty("user.dir");
	public static String dbFile = projectDir + "/rutracker.db";

	public static void main(String[] args) {
		//List<TorrentItem> programmingVideos = retrieveAndPrintTorrents("http://rutracker.org/forum/viewforum.php?f=1565&start=", 2950); // Programming videos
		//persistTorrentsList(programmingVideos, "ProgrammingVideos");

		//List<TorrentItem> webDesignVideos = retrieveAndPrintTorrents("http://rutracker.org/forum/viewforum.php?f=1564&start=", 1000); // Web Design videos
		//persistTorrentsList(webDesignVideos, "WebDesignVideos");

		//retrieveAndPrintTorrents("http://rutracker.org/forum/viewforum.php?f=1426&start=", 3200); // Programming books
		//retrieveAndPrintTorrents("http://rutracker.org/forum/viewforum.php?f=1425&start=", 1950); // Web design and programming books

		// ---------------------------- Conditional searches
		retrieveAndPrintTorrents("http://rutracker.org/forum/viewforum.php?f=1426&start=", 3200, " r "); // R books
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

			String sql = String.format("insert into %s(title, size, flag, other) values ", tableName);
			for(TorrentItem item : items){
				if(!startsInList(existingTorrents, item.title)){
					sql += String.format(" ('%s', '%s', %s, '%s'),",
							item.title, item.size, item.flag, item.other);
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

		WebDriver driver = new HtmlUnitDriver();
		int page = 1, counter = 1, offset = 0;
		List<String> items = new ArrayList<String>();
		List<TorrentItem> result = new ArrayList<TorrentItem>();

		while(offset <= count){
			if(containsConditions.length ==0){
				System.out.println("============== Page " + page + " ===================+++++============");
			}

			driver.get(url+offset);
			List<WebElement> rows = driver.findElements(By.tagName("tr"));
			for(WebElement elem : rows){
				try{
					String title = elem.findElements(By.cssSelector("a")).get(0).getText();
					String size = elem.findElements(By.cssSelector("td")).get(2).getText();

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
						if(print){
							title = title.replaceAll("'", "\"");
							TorrentItem item = new TorrentItem().setId(counter).setTitle(title).setSize(size).setFlag(0).setOther("");
							if(items.contains(title)){
								item.setOther("  -->DUPLICATE +(# " + items.indexOf(title)+ ")");
							}
							items.add(title);
							result.add(item);
							System.out.println(counter + ") " +  title + "      " + "[" + size + "]");
							counter++;
						}
				}catch(Exception e){
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
}