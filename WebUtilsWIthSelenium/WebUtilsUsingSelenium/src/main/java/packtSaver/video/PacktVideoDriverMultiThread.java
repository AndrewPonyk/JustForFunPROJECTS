package packtSaver.video;

import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PacktVideoDriverMultiThread extends PacktVideoDriver {
    ExecutorService threadPool = Executors.newFixedThreadPool(5);

    @Override
    protected void saveFiles(String courseName, Map<String, List<Pair<String, String>>> sectionUrls) {
        String folder = PACKT_VIDEO_DOWNLOAD_PATH + preprocessFilenameRemoveCharacters(courseName) + "\\";

        totalVideos.set(sectionUrls.values().stream().map(List::size).reduce(0, Integer::sum));
        System.out.println("TOTAL: videos" + totalVideos.get());

        final Map<String, List<Pair<String, String>>> sectionVideosSrc = getSectionVideosSrc(sectionUrls);
        final File rootFolder = new File(folder);
        if (rootFolder.exists()) {
            System.out.println("ROOT already existss!!");
            //throw new RuntimeException("Cannot create root folder ALREADY CREATED" + folder);

        } else {
            final boolean root = rootFolder.mkdir();
            if (!root) {
                throw new RuntimeException("Cannot create root folder " + folder);
            }
        }


        sectionVideosSrc.keySet().forEach(item -> {
            String sectionFolder = folder + preprocessFilenameRemoveCharacters(item);
            System.out.println(sectionFolder);
            final File file = new File(sectionFolder);
            if (!file.exists()) {
                file.mkdir();
            } else {
                System.out.println(sectionFolder + ": section folder already present");
            }

            threadPool.execute(() -> downloadSection(sectionVideosSrc, item, sectionFolder));
        });

        threadPool.shutdown();

        while (!threadPool.isTerminated()) {
            String test = "";
        }
        System.out.println("Finished all threads");
    }

    //this method is executed in separate thread  (thread pool with 5 threads in total)
    private void downloadSection(Map<String, List<Pair<String, String>>> sectionVideosSrc, String item, String sectionFolder) {
        sectionVideosSrc.get(item).forEach((Pair<String, String> link) -> {

            String filename = sectionFolder + "\\" +
                    preprocessFilenameRemoveCharacters(link.getLeft()) + ".mp4";
            String mp4Url = link.getRight();

            final File video = new File(filename);
            System.out.println("Download " + Thread.currentThread().getName() + " " + link.getRight());
            if (video.exists()) {
                coundDone.incrementAndGet();
                System.out.println(" file already exists: " + filename + "");
                System.out.println(coundDone.incrementAndGet() + " videos done of " + totalVideos);

                return;
            } else {

            }
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
            System.out.println(coundDone.incrementAndGet() + " videos done of " + totalVideos);
        });
    }

    private Map<String, List<Pair<String, String>>> getSectionVideosSrc(Map<String, List<Pair<String, String>>> sectionUrls) {
        Map<String, List<Pair<String, String>>> result = new LinkedHashMap<>();
        sectionUrls.keySet().forEach(sectionKey -> {
            final List<Pair<String, String>> section = sectionUrls.get(sectionKey);
            final List<Pair<String, String>> sectionWithVideoSrc = new LinkedList<>();
            section.forEach(item -> {
                packtWebDriver.get(item.getRight());
                WebElement video = second7wait.until(driver -> driver.findElement(By.cssSelector("video.vjs-tech")));

                sectionWithVideoSrc.add(Pair.of(item.getLeft(), video.getAttribute("src")));
            });

            result.put(sectionKey, sectionWithVideoSrc);
        });
        return result;
    }


}
