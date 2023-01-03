package packtSaver.video;

import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import packtSaver.book.PacktDriver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PacktVideoDriver extends PacktDriver {
    protected AtomicInteger totalVideos = new AtomicInteger(0);
    protected AtomicInteger coundDone = new AtomicInteger(0);

    public static String PACKT_VIDEO_DOWNLOAD_PATH = "F:\\tmp\\packt\\video\\";

    static {

    }
    private List<RemoteWebDriver> oldDrivers = new ArrayList<>();

    public void saveVideo(String url) {
        if (url != null) {
            packtWebDriver.get(url);
            sleep(2000);
        }

        try {
            final WebElement barIcon = packtWebDriver.findElement(By.cssSelector(".bar-icon"));
            if (barIcon != null) {
                barIcon.click();
            }
        } catch (Exception e) {
        }

        String courseName = "undefined";
        try {
            courseName = packtWebDriver.findElement(By.cssSelector(".book-title")).getText();
        } catch (Exception e) {
            System.err.println("cant retrieve course name");
            throw e;
        }

        Map<String, List<Pair<String, String>>> sectionUrls = getSectionUrls();
        System.out.println("----");
        System.out.println(sectionUrls);
        System.out.println("-----");
        saveFiles(courseName, sectionUrls);
    }

    protected void saveFiles(String courseName, Map<String, List<Pair<String, String>>> sectionUrls) {
        String folder = PACKT_VIDEO_DOWNLOAD_PATH + preprocessFilenameRemoveCharacters(courseName) + "\\";

        final File rootFolder = new File(folder);
        if (rootFolder.exists()) {
            throw new RuntimeException("Cannot create root folder ALREADY CREATED" + folder);
        }
        final boolean root = rootFolder.mkdir();
        if (!root) {
            throw new RuntimeException("Cannot create root folder " + folder);
        }

        sectionUrls.keySet().forEach(item -> {
            String sectionFolder = folder + preprocessFilenameRemoveCharacters(item);
            System.out.println(sectionFolder);
            new File(sectionFolder).mkdir();

            sectionUrls.get(item).forEach((Pair<String, String> link) -> {
                packtWebDriver.get(link.getRight());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final WebElement video = packtWebDriver.findElement(By.cssSelector("video.vjs-tech"));

                String filename = sectionFolder + "\\" +
                        // counter.getAndIncrement() + //  moved this logic to retrieve
                        preprocessFilenameRemoveCharacters(link.getLeft()) + ".mp4";

                String mp4Url = video.getAttribute("src");
                System.out.println(filename);
                System.out.println(mp4Url);

                try (BufferedInputStream in = new BufferedInputStream(new URL(mp4Url).openStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(filename)) {
                    byte dataBuffer[] = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    protected Map<String, List<Pair<String, String>>> getSectionUrls() {
        Map<String, List<Pair<String, String>>> result = new LinkedHashMap<>();

        WebElement tocParentElement = packtWebDriver.findElements(By.cssSelector(".toc-container")).get(0);

        //open all sections
        List<WebElement> sectionsElements = tocParentElement.findElements(By.cssSelector(".accordion"));
        sectionsElements.stream().forEach(element -> {
            try {
                //System.out.println("||||"+element.getAttribute("outerHTML")+"||||");
                if (!element.getAttribute("class").contains("colla") && !element.getAttribute("innerHTML").contains("collapsed")) {
                    element.click();
                    Thread.sleep(250);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        sectionsElements.forEach(section -> {
            final List<Pair<String, String>> sectionVideos = new ArrayList<>();
            String sectionText = section.findElements(By.cssSelector(".card-header")).get(0).getText();
            sectionText = sectionText.replaceAll("\n", "");
            final AtomicInteger counter = new AtomicInteger(1);
            section.findElements(By.cssSelector("li a")).forEach(e -> {
                Pair<String, String> linkPair = Pair.of(counter.getAndIncrement() + e.getText(), e.getAttribute("href"));
                sectionVideos.add(linkPair);
            });
            result.put(sectionText, sectionVideos);
        });
        return result;
    }

    protected String preprocessFilenameRemoveCharacters(String name) {
        return name.replaceAll(":", "")
                .replaceAll("\n", "")
                .replaceAll("/", "-")
                .replaceAll("\\?", "")
                .replaceAll("\\*", "")
                .replaceAll(">", "")
                .replaceAll("<", "")
                .replaceAll("\"", "");
    }

    public static void main(String[] args) {
        System.out.println("test?".replaceAll("\\?",""));
    }
}