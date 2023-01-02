package packtSaver.video;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class OptimizeQualityVideoApp {
    final ExecutorService executorService = Executors.newFixedThreadPool(2);
    final AtomicInteger counter = new AtomicInteger(0);
    AtomicInteger total = new AtomicInteger(0);

    public static void main(String[] args) throws IOException, InterruptedException {
        final long start = System.currentTimeMillis();
        System.out.println("START: " + start);
        System.out.println("Use ffmpeg to reduce video size");
        final OptimizeQualityVideoApp optimizeQualityVideoApp = new OptimizeQualityVideoApp();
        optimizeQualityVideoApp.optimizeVideos("F:\\tmp\\packt\\video\\devops-project-video");
        optimizeQualityVideoApp.shutdownExecutorService();
        final long end = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (end - start) / 1000 + " seconds");
    }

    public void optimizeVideos(String rootPath) throws IOException, InterruptedException {
        File root = new File(rootPath);
        final File[] files = root.listFiles();

        for (File f : files) {
            if (f.isDirectory()) {
                optimizeVideos(f.getAbsolutePath());
            } else if (f.getAbsolutePath().endsWith("mp4") && !f.getAbsolutePath().contains("-ffmpeg")) {
                total.incrementAndGet();
                executorService.execute(()-> {
                    try {
                        optimizeSingleVideo(f.getAbsolutePath());
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });

            }
        }

    }

    private void shutdownExecutorService() {
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            String test="1";
        }
        System.out.println("Finished all threads");
    }

    public void optimizeSingleVideo(String path) throws IOException, InterruptedException {
        System.out.println(Thread.currentThread().getName() + " start optimize" + path);
        String command =
                " D:\\WindowsPrograms\\ffmpeg\\ffmpeg-master-latest-win64-gpl\\bin\\ffmpeg.exe -i "
                        + "\"" + path + "\"" +
                        " " +
                        "\"" + path.replaceAll("\\.mp4", "\\-ffmpeg.mp4") + "\"";

        ProcessBuilder processBuilder = new ProcessBuilder();
        // Windows
        processBuilder.command("cmd.exe", "/c", command);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(new File("C:\\tmp\\22222.txt")); //HELPS  A LOT!!!
        System.err.println(String.join(" ", processBuilder.command().toArray(new String[0])));

        Process process = processBuilder.start();

        int exitCode = process.waitFor();
        System.out.println("\nExited with error code : " + exitCode);
        counter.incrementAndGet();
        System.out.println("DONE: " + counter.get() + " OF " + total);
        System.out.println("----");
    }
}