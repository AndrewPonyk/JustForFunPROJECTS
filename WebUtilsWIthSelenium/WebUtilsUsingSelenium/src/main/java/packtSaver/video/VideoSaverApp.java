package packtSaver.video;

public class VideoSaverApp {
    public static void main(String[] args) {
        PacktVideoDriver driver = new PacktVideoDriver();
        driver.login(true);

        driver.saveVideo("https://www.packtpub.com/mapt/video/big_data_and_business_intelligence/9781788995580");
    }
}
