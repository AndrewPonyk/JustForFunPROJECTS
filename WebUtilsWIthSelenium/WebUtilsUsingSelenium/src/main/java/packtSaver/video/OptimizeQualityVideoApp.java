package packtSaver.video;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class OptimizeQualityVideoApp {
    public static final String FFMPEG_EXE_I = " C:\\Programs\\ffmpeg\\ffmpeg-master-latest-win64-gpl\\bin\\ffmpeg.exe -i  "; // path to ffmpeg

    public static final String THREADS_FFMPEG = "-threads 3"; // number of threads for ffmpeg (it supports multithreading if there are multiple cpus)

    final ExecutorService executorService = Executors.newFixedThreadPool(3); // put it TO ONE - to avoid pc overload
    final AtomicInteger counter = new AtomicInteger(0);
    AtomicInteger total = new AtomicInteger(0);
 
    public static void main(String[] args) throws IOException, InterruptedException {
        final long start = System.currentTimeMillis();
         System.out.println("START: " + start + "\n Use ffmpeg to reduce video size");
        final OptimizeQualityVideoApp optimizeQualityVideoApp = new OptimizeQualityVideoApp();

        final String path = "F:\\tmp\\packt\\kotlin-beginners-learn-programming";
        optimizeQualityVideoApp.optimizeVideos(path); 
        optimizeQualityVideoApp.shutdownExecutorServiceAndWait();
        if (optimizeQualityVideoApp.checkAllFilesOptimized(path)) { 
            System.out.println("Renaming root"); 
            new File(path).renameTo(new File(path + "--ffmpeg"));
        }

        final long end = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (end - start) / 1000 + " seconds");
    }


    
    private boolean checkAllFilesOptimized(String path) {
        return true;
    }

    public void optimizeVideos(String rootPath) throws IOException, InterruptedException {
        File root = new File(rootPath);
        if (rootPath.contains("-ffmpeg")) {
            System.err.println("Already OPTIMIZED!!! But still lets iterate and check all videos");
        }

        final File[] files = root.listFiles();

        for (File f : files) {
            if (f.isDirectory()) {
                optimizeVideos(f.getAbsolutePath());
            } else if (f.getAbsolutePath().toLowerCase().endsWith("mp4") && !f.getAbsolutePath().contains("-ffmpeg") ) {
                total.incrementAndGet();
                executorService.execute(() -> {
                    try {
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
        System.out.println(Thread.currentThread().getName() + " " + LocalDateTime.now() + " start optimize" + path);
        String newFileName = path.replaceAll("\\.mp4", "\\-ffmpeg.mp4");

        newFileName = newFileName.replaceAll("\\.MP4", "\\-ffmpeg.mp4");

        String command =
                FFMPEG_EXE_I
                        + "\"" + path + "\"" +
                        " "
                        + THREADS_FFMPEG
                        + " "
                        + "\"" + newFileName + "\"";

        ProcessBuilder processBuilder = new ProcessBuilder();
        // Windows
        processBuilder.command("cmd.exe", "/c", command);
        processBuilder.redirectErrorStream(true);
        System.err.println(String.join(" ", processBuilder.command().toArray(new String[0])));

        Process process = processBuilder.start();

        InputStream inputStream = process.getInputStream();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = reader.readLine()) != null) {
            if( line.contains("frame=") ) {
                if( line.contains("8") && line.contains("6") && (line.contains("25") || line.contains("24") || line.contains("23") || line.contains("22")) ){
                    System.out.println(">> " + path + " >>>>" +  line); // Print some lines  to the console
                    System.out.println(Thread.currentThread().getName() + LocalDateTime.now() + "  " + "DONE: " + counter.get() + " OF " + total);
                }
            } else {
                //System.out.println(">>>"+line); // Print each line to the console
            }
        }

        int exitCode = process.waitFor();
        System.out.println("\nExited with error code : " + exitCode);
        counter.incrementAndGet();
        System.out.println(Thread.currentThread().getName() + LocalDateTime.now() + "  " + "DONE: " + counter.get() + " OF " + total);

        //remove old file also!!!
        final File oldFile = new File(path);
        final File newFile = new File(newFileName);
        if (newFile.exists() && Files.size(Paths.get(newFileName)) > 1000) {
            System.out.println("Removing old file: " + path);
            oldFile.delete();
        } else {
            throw new RuntimeException("New file do not exists: " + newFileName);
        }
        System.out.println("----");
    }

    public static boolean isPrime(int n ){
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i < n; i++) { // i = 2
            if (n % i == 0) { // 5 % 2 == 0
                return false;
            }
        }
        return true;

    }


    public static String findSumOfAllDigitsInString(String str) {
        int sum = 0;
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            if (Character.isDigit(c)) {
                sum += Integer.parseInt(String.valueOf(c));
            }
        }

        //create double value of sum
        double d = Double.parseDouble(String.valueOf(sum));
        //check if sum is prime number
        if (isPrime(sum)) {
            //if sum is prime number, then add 1 to sum
            d += 1;
        }



        return String.valueOf(sum);
    }

    //what this method does?

    // This code calculates the factorial of an integer n, by
    // multiplying all the numbers from 1 to n together.
    //
    // For example, if n = 5, then the factorial is 1 * 2 * 3 * 4 * 5 = 120.

    public static int calculateFactorial(int n) {
        //refactor this method using recursion
        //initialize constant a with value 10
        if (n == 0 || n == 1) {
            return 1;
        } else {
            return n * calculateFactorial(n - 1);
        }
    }


    //explain line by line what this medthod does

    public static void printHelloWorld() {
        for (int i = 0; i < 10; i++) {
            //print also index (concatenate with hello world)
            //add printing of index
            //print also lenght of string

            System.out.println("Hello world");
        }
    }

    //q: what this method does?

    //create constant A with value 10
    public static final int A = 10;

    String[] top10WorldCitiesByPopulation = new String[]{
            "Tokyo",
            "Delhi",
            "Shanghai",
            "Sao Paulo",
            "Mexico City",
            "Cairo",
            "Dhaka",
            "Mumbai",
            "Beijing",
            "Osaka"
    };
}