package vtrack.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import vtrack.Application;

public class VtrackUtils {
	
	private final FirefoxDriver firefox;
	public static final String HOME_DIRECTORY = "/home/andrew/git/JustForFunPROJECTS/VTrack/dest/";
	public static final String DATA_DIRECTORY = HOME_DIRECTORY+ "v_data/";
	
	public static final String VK_END_POINT = "http://vk.com/";
	public static final String[] TRACKING_IDS = { "andrew99999", "id11141239", "id56735380" };

	private DateTime currentDate = new DateTime();
	
	public VtrackUtils() {

		// will work only in windows (in linux there is no tray (( )
		File file = new File("C:\\minimizetray.xpi");

		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("permissions.default.image", 2);
		try {
			if (file.exists()) {
				profile.addExtension(file);
				profile.setPreference("extensions.mintrayr.minimizeon", 1); // in mozille type in url bar																			// 'about:config'
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		firefox = new FirefoxDriver(profile);
	}

	public void startTracking() {
		loginAndStartTracking();
	}

	public void loginAndStartTracking() {
		firefox.get("http://vk.com/");

		System.out.println(firefox.getTitle());
		firefox.findElement(By.id("quick_email")).sendKeys(
				"stepanko-kader@mail.ru");
		firefox.findElement(By.id("quick_pass")).sendKeys("ghblehjr1");
		firefox.findElement(By.id("quick_login_button")).click();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// now logged in 
		
		
		UserSession [] usersSessions = new UserSession[TRACKING_IDS.length];
		for(int i = 0 ;i<TRACKING_IDS.length;i++){
			usersSessions[i] = new UserSession();
		}
		
		for (int j = 0;;) {

			for (int i = 0; i < TRACKING_IDS.length; i++) {
				System.out.println("checking. " + TRACKING_IDS[i] + new DateTime());
				firefox.get(VK_END_POINT + TRACKING_IDS[i]);

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (firefox.findElements(By.id("profile_time_lv")).size() != 0) {
					String leave = firefox.findElements(By.id("profile_time_lv")).get(0).getText();
					System.out.println(leave);
					
					if(leave.length() > 0 && !leave.contains(":") && usersSessions[i].online){
						leave = leave.replaceAll("\\D", "");
						usersSessions[i].online = false;
						usersSessions[i].end = new DateTime().minusMinutes(new Integer(leave));
						System.out.println("#Prepending " + usersSessions[i].end + " " + leave );
						System.out.println("#Prependings : " + " " + new DateTime().minusMinutes(new Integer(leave)));
												
						try {
							System.out.println("*** appending to : " + DATA_DIRECTORY + TRACKING_IDS[i]+".txt");
							System.out.println("##### Appending : " + usersSessions[i].toString());
							FileUtils.write(new File(DATA_DIRECTORY + TRACKING_IDS[i]+".txt"), usersSessions[i].toString(), true);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				} else if (firefox.findElements(By.id("profile_online_lv"))
						.size() > 0
						&& firefox.findElements(By.id("profile_online_lv"))
								.get(0).isDisplayed()) {
					// probably online...
					System.out.println("online"
							+ firefox.findElements(By.id("profile_online_lv"))
									.get(0).getText());
					if( !usersSessions[i].online){
						usersSessions[i].start = new DateTime();
						usersSessions[i].online = true;
					}
				}
				System.out.println(firefox.getTitle() + "\n------------");
			}
			System.out.println("\n------------------------------");
			try {
				Thread.sleep(57000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void logData() {
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("utils");
		System.out.println(HOME_DIRECTORY + TRACKING_IDS[0]+".txt");
		FileUtils.write(new File(HOME_DIRECTORY + TRACKING_IDS[0]+".txt"), "hello", true);
	}
}

class UserSession{
	public DateTime start;
	public DateTime end;
	public boolean online = false;
	
	public UserSession(){
	}
	
	@Override
	public String toString() {
		return start.getHourOfDay() + ":" + start.getMinuteOfHour() + "-"
				+ end.getHourOfDay() + ":" + end.getMinuteOfHour() + " ";
	}
}