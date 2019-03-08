package scanlibsdownload;

import java.io.File;

public class ScanlibsDownloadApp {
    public static void main(String[] args) {
        System.out.println("Download scanlibs resources");
        ScanlibsDownloadBrowser.download("https://scanlibs.com/page/%s/?s=angular");
    }


}
