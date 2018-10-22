package packtSaver.video;

public class VideoSaverApp {
    public static void main(String[] args) {
        PacktVideoDriver driver = new PacktVideoDriver();
        driver.login(true);

        driver.saveVideo("https://www.packtpub.com/mapt/video/application_development/9781789342604");
    }
}
