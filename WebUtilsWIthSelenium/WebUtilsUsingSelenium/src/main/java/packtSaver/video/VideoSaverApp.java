package packtSaver.video;

public class VideoSaverApp {
    public static void main(String[] args) {
        System.out.println("Save video..");
        //PacktVideoDriver driver = new PacktVideoDriver();
        PacktVideoDriver driver = new PacktVideoDriverMultiThread();

        //driver.login(true);
        driver.saveVideo("https://subscription.packtpub.com/video/programming/9781804618486/p1/video1_1/what-is-programming#_ga=2.249865603.488796246.1672225844-2131929782.1659217675");
    }
}
