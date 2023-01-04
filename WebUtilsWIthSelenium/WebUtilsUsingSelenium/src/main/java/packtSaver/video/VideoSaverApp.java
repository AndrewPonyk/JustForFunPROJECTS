package packtSaver.video;

public class VideoSaverApp {
    public static void main(String[] args) {
        System.out.println("Save video..");
        //PacktVideoDriver driver = new PacktVideoDriver();
        PacktVideoDriver driver = new PacktVideoDriverMultiThread();

        //driver.login(true);
        driver.saveVideo("https://subscription.packtpub.com/video/web-development/9781803234953/p4/video4_1/create-and-customize-projects-part-1#_ga=2.158548415.488796246.1672225844-2131929782.1659217675");
    }
}
