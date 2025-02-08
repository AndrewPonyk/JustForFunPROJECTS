package packtSaver.video;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FilenameUtils;

public class OptimizeQualityVideoApp {
    public static final String FFMPEG_EXE_I = " C:\\Programs\\ffmpeg\\ffmpeg-master-latest-win64-gpl\\bin\\ffmpeg.exe  -i  "; // path
                                                                                                                              // to
                                                                                                                              // ffmpeg
    public static final String THREADS_FFMPEG = "-threads 3"; // number of threads for ffmpeg (it supports
                                                              // multithreading if there are multiple cpus)

    public final ExecutorService executorService = Executors.newFixedThreadPool(1); // put it TO two - to avoid 100% CPU
                                                                                    // usage
    public final AtomicInteger counter = new AtomicInteger(0);
    public AtomicInteger total = new AtomicInteger(0);

    public static boolean compressAlreadyCompressed = false;

    public static List<String> compressionTypes = Arrays.asList("CPU compression", "Usual full HD/HD compression",
            "45678k compression");

    public static String typeOfCompression = "";

    public static long inputFilesSize = 0;
    public static long outputFilesSize = 0;

    public static List<String> finalLogItems = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException { //таїд 
        final long start = System.currentTimeMillis();
        final String path = "D:\\D_compress"; // path to folder with videos
        if (args.length > 0) { // read type of compression from user input
            typeOfCompression = args[0];
        } else {
            System.out.println("Please provide type of compression: ");
            for (int i = 0; i < compressionTypes.size(); i++) {
                System.out.println(i + " : " + compressionTypes.get(i));
            }
            System.out.println("Path is :" + path);
            System.out.println("Enter number: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            typeOfCompression = reader.readLine();
        }

        System.out.println("START: " + start + "\n Use ffmpeg to reduce video size");
        final OptimizeQualityVideoApp optimizeQualityVideoApp = new OptimizeQualityVideoApp();

        optimizeQualityVideoApp.compressAlreadyCompressed = false;
        

        optimizeQualityVideoApp.optimizeVideos(path);
        optimizeQualityVideoApp.shutdownExecutorServiceAndWait();
        if (optimizeQualityVideoApp.checkAllFilesOptimized(path)) {
            System.out.println("Renaming root");
            new File(path).renameTo(new File(path + "--ffmpeg"));
        } else {
            System.err.println("Some files are not optimized===");
        }

        final long end = System.currentTimeMillis();
        System.out.println("Final log:");
        System.out.println("Total final files: " + finalLogItems.size());
        for (String item : finalLogItems) {
            System.out.println(item);
        }
        System.out.println("Input files size: " + inputFilesSize / 1024 / 1024 + " MB");
        System.out.println("Output files size: " + outputFilesSize / 1024 / 1024 + " MB");
        System.out.println("Time elapsed: " + (end - start) / 1000 + " seconds");
    }

    private boolean checkAllFilesOptimized(String path) {
        // check all files with extension .mp4(MP4) and .ts(TS) are optimized (сontains
        // -ffmpeg)
        File root = new File(path);
        final File[] files = root.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                if (!checkAllFilesOptimized(f.getAbsolutePath())) {
                    return false;
                }
            } else if (f.getAbsolutePath().toLowerCase().endsWith("mp4")
                    || f.getAbsolutePath().toLowerCase().endsWith("ts")) {
                if (!f.getAbsolutePath().contains("-ffmpeg")) {
                    return false;
                }
            }
        }
        return true;
    }

    public void optimizeVideos(String rootPath) throws IOException, InterruptedException {
        File root = new File(rootPath);
        if (rootPath.contains("-ffmpeg") && !compressAlreadyCompressed) {
            System.err.println(rootPath + "Already OPTIMIZED!!!");
            return;
        }

        final File[] files = root.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                if (!f.getName().contains("-ffmpeg")) {
                    optimizeVideos(f.getAbsolutePath());
                }
            } else if ((f.getAbsolutePath().toLowerCase().endsWith("mp4")
                    || f.getAbsolutePath().toLowerCase().endsWith("ts")) 

                    && !f.getAbsolutePath().toLowerCase().contains("-broken")
                    
                    && f.length() > 1000000
                    
                    && (!f.getAbsolutePath().contains("-ffmpeg") || compressAlreadyCompressed)) { 
                        // mp4 and ts files        (ts is also extension for video  files)
                total.incrementAndGet();
                executorService.execute(() -> {
                    try {
                        System.out.println("Submitting: " + f.getAbsolutePath());
                        optimizeSingleVideo(f.getAbsolutePath());
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });

            } else if (f.getAbsolutePath().contains("-ffmpeg")) {
                System.out.println("> Skip: " + f.getAbsolutePath());
            }
        }
    }

    private void shutdownExecutorServiceAndWait() {
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            String test = "1";
        }
        System.out.println("Finished all threads");
    }

    public void optimizeSingleVideo(String path) throws IOException, InterruptedException {
        System.out.println(Thread.currentThread().getName() + " " + LocalDateTime.now() + " start optimize " + path);
        String newFileName = path.replaceAll("\\.mp4", "\\-ffmpeg.mp4");
        newFileName = newFileName.replaceAll("\\.MP4", "\\-ffmpeg.mp4");

        if (path.toLowerCase().endsWith(".ts")) {
            newFileName = newFileName.replaceAll("\\.ts", "\\-ffmpeg.ts"); // ts is also video file
            newFileName = newFileName.replaceAll("\\.TS", "\\-ffmpeg.ts"); // ts is also video file
        }

        if (new File(newFileName).exists()) {
            System.out.println("!!!!! FFmpeg File already exists: " + newFileName);
            return;
        }

        String command = FFMPEG_EXE_I
                + "\"" + path + "\"" +
                " "
                + THREADS_FFMPEG
                + " -vf scale=-1:720 "
                + " -c:v libx264 -crf 29 -preset medium " // todo : retrieve video resolution from file first
                + "\"" + newFileName + "\"";

        // full hd
        if (compressionTypes.get(Integer.valueOf(typeOfCompression)).equals("Usual full HD/HD compression")) {
            command = FFMPEG_EXE_I.replace("-i", "-fflags +discardcorrupt -err_detect ignore_err -i")
                    + "\"" + path + "\""
                    + " -c:v libx264 -crf 24 -preset medium "
                    + "\"" + newFileName + "\"";
        }

        // 8k cuda
        if (compressionTypes.get(Integer.valueOf(typeOfCompression)).equals("45678k compression")) {
            // TODO: check if datarate is over 30mbps
            int frameWidth = 0;
            command = FFMPEG_EXE_I.replace("-i", "-fflags +discardcorrupt -err_detect ignore_err -i")
                    + "\"" + path + "\""
                    + " -c:v hevc_nvenc -crf 13 -preset medium -b:v 28000k "
                    + "\"" + newFileName + "\"";

            if (frameWidth > 7200) {
                command = FFMPEG_EXE_I.replace("-i", "-fflags +discardcorrupt -err_detect ignore_err -i")
                        + "\"" + path + "\""
                        + " -c:v hevc_nvenc -crf 13 -preset medium -b:v 28500k "
                        + "\"" + newFileName + "\"";
            } else if (frameWidth > 6100) {
                command = FFMPEG_EXE_I.replace("-i", "-fflags +discardcorrupt -err_detect ignore_err -i")
                        + "\"" + path + "\""
                        + " -c:v hevc_nvenc -crf 13 -preset medium -b:v 24500k "
                        + "\"" + newFileName + "\"";
            } else if (frameWidth > 5150) {
                command = FFMPEG_EXE_I.replace("-i", "-fflags +discardcorrupt -err_detect ignore_err -i")
                        + "\"" + path + "\""
                        + " -c:v hevc_nvenc -crf 13 -preset medium -b:v 21000k "
                        + "\"" + newFileName + "\"";
            } else if (frameWidth > 4000) {
                command = FFMPEG_EXE_I.replace("-i", "-fflags +discardcorrupt -err_detect ignore_err -i")
                        + "\"" + path + "\""
                        + " -c:v hevc_nvenc -crf 13 -preset medium -b:v 19000k "
                        + "\"" + newFileName + "\"";
            }

        }

        ProcessBuilder processBuilder = new ProcessBuilder();
        // Windows
        processBuilder.command("cmd.exe", "/c", command);
        processBuilder.redirectErrorStream(true);
        System.err.println(String.join(" ", processBuilder.command().toArray(new String[0])));

        Process process = processBuilder.start();

        int printCounter = 0;
        InputStream inputStream = process.getInputStream();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = reader.readLine()) != null) {
            if (/*printCounter <= 25 ||*/ printCounter % 150 == 0) {
                System.out.println(">> " + path + " >>>>" + line); // Print some lines to the console
                System.out.println(Thread.currentThread().getName() + LocalDateTime.now() + "  " + "DONE: "
                        + counter.get() + " OF " + total);
            }
            printCounter++;
        }

        int exitCode = process.waitFor();

        System.out.println("\nExited with error code : " + exitCode );
        counter.incrementAndGet();
        System.out.println(Thread.currentThread().getName() + LocalDateTime.now() + "  " + "DONE: " + counter.get()
                + " OF " + total);

        // remove old file also!!!
        final File oldFile = new File(path);
        final File newFile = new File(newFileName);

        if (newFile.exists() && exitCode == 0) {
            inputFilesSize += oldFile.length();

            long newFileSize = Files.size(Paths.get(newFileName));
            long oldFileSize = Files.size(Paths.get(path));

            if (newFileSize > oldFileSize) {
                System.out.println("Removing new file: " + newFileName + " because size is bigger :" + newFileSize
                        + " is bigger than" + oldFileSize); // Correct: Remove new file
                newFile.delete();

                long newFileSizeKB = newFileSize / 1024;

                String extension = FilenameUtils.getExtension(path);
                // Rename old file (optional, but what you wanted)
                String renamedFileName = FilenameUtils.removeExtension(path) + "-FBEcame-" + newFileSizeKB
                        + "kB--ffmpeg" + "." + extension;
                File renamedFile = new File(renamedFileName);
                if (oldFile.renameTo(renamedFile)) {
                    outputFilesSize += oldFileSize;
                    System.out.println("Renamed old file to: " + renamedFileName);
                } else {
                    throw new RuntimeException("Failed to rename old file to: " + renamedFileName);
                }

            } else {
                if (Files.size(Paths.get(newFileName)) > 10000) {
                    // todo: check if duration is the same
                    outputFilesSize += newFileSize;
                    System.out.println("Removing old file: " + path);
                    //print old and new file sizes in mb
                    System.out.println("Old file size: " + oldFileSize/1024/ 1024 + "mb New file size: " + newFileSize/1024/1024 + "mb");
                    
                    oldFile.delete(); // Remove old file if it's smaller or equal
                }
            }
            finalLogItems.add("Exit code: " + exitCode + " for file: " + path + " new file: " + newFileName + " size: "
                    + newFileSize + " old file size: " + oldFileSize);
        } else {
            System.out.println("New file does not exist or is broken: " + newFileName);
            // throw new RuntimeException("New file does not exist or is broken: " +
            // newFileName);
            finalLogItems
                    .add("Exit code: " + exitCode + " for file: " + path + " new file does not exist " + newFileName);
        }
        System.out.println("----");
    }   

    public static Map<String, String> getVideoInfo(String path) {
        
        if (path == null || path.isEmpty()) {
            return new HashMap<>();
        }
        
        Map<String, String> videoInfo = new HashMap<>();
        
        // Command to get total bitrate
        String bitrateCommand = String.format(
            "ffprobe -v error -show_entries format=bit_rate -of default=noprint_wrappers=1 \"%s\"",
            path
        );
    
        // Command to get stream-specific parameters
        String streamCommand = String.format(
            "ffprobe -v error -show_entries stream=width,height,codec_name -of default=noprint_wrappers=1 \"%s\"",
            path
        );
    
        try {
            // Execute bitrate command
            Process bitrateProcess = Runtime.getRuntime().exec(bitrateCommand);
            BufferedReader bitrateReader = new BufferedReader(new InputStreamReader(bitrateProcess.getInputStream()));
            String line;
            while ((line = bitrateReader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    videoInfo.put(parts[0], parts[1]);
                }
            }
            bitrateReader.close();
    
            // Execute stream command
            Process streamProcess = Runtime.getRuntime().exec(streamCommand);
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(streamProcess.getInputStream()));
            while ((line = streamReader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    videoInfo.put(parts[0], parts[1]);
                }
            }
            streamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return videoInfo;
    }
}