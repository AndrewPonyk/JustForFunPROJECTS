package pdfjoiner;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class ScanLibsPdfJoiner {
    protected RemoteWebDriver packtWebDriver;

    public static void main(String[] args) {

        new ScanLibsPdfJoiner();
    }

    public ScanLibsPdfJoiner() {
        //
        System.setProperty("webdriver.chrome.driver", driver());

        packtWebDriver = new ChromeDriver();

        System.out.println("Merging pdf documents");
        List<String> urls = new LinkedList<>();

        IntStream.range(1, 3).forEach(i ->
        urls.add("https://scanlibs.com/page/"+i));

        for (String url : urls){
            takeScreenshot(url);
        }

        convertImagesToPdf("scanlibs_ALL");
    }

    public static String convertImagesToPdf(String filename) {
        final Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("/home/andrii/temp/packtimages/" + filename+ ".pdf"));
            document.open();
            File[] files = new File("/home/andrii/temp/packtimages").listFiles();
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));

            IntStream.range(0, files.length).forEach(i -> {
                try {
                    Image image = Image.getInstance(files[i].getAbsolutePath());
                    //Setting zero, zero - cause some unpredictible location,
                    image.setAbsolutePosition(1, 1);
                    document.setPageSize(new Rectangle(image.getWidth(), image.getHeight()+2));
                    document.newPage();
                    document.add(image);
                    System.out.println("Adding " + i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String takeScreenshot(String url) {
        if (url != null) {
            packtWebDriver.get(url);
            sleep(4500);
        }
        //removeUnusedElements();
       // expandScrollables();
        packtWebDriver.executeScript("window.scrollTo(0, arguments[0]);", 0);
        sleep(500);
     //   removeSideBar();
        sleep(1000);
        String js = "return Math.max( document.body.scrollHeight, document.body.offsetHeight,  document.documentElement.clientHeight,  document.documentElement.scrollHeight,  document.documentElement.offsetHeight);";
        Integer scrollheight = new Integer(packtWebDriver.executeScript(js).toString());

        List<BufferedImage> slices = new ArrayList<>();
        Integer offset = 0;
        List<Integer> offset_arr = new ArrayList<>();
        int off = 0;
        System.out.println("Scroll height:" + scrollheight);
        int counter = 1;
        while (offset < (scrollheight - 260)) {
            if (offset > scrollheight) {
                packtWebDriver.executeScript("window.scrollTo(0, arguments[0]);", scrollheight);
            } else {
                packtWebDriver.executeScript("window.scrollTo(0, arguments[0]);", offset);
            }
            sleep(600);
            File file = packtWebDriver.getScreenshotAs(OutputType.FILE);

            BufferedImage img = null;

            try {
                img = ImageIO.read(file);
                slices.add(img);
                FileUtils.copyFile(file, new File("/home/andrii/temp/packtimages/" + url
                        .replaceAll("\\.","")
                        .replaceAll("/", "")
                        .replace(":", "")
                        + counter++ +".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            offset += img.getHeight();
        }

//        int x = 0, y = 0;
//        BufferedImage result = new BufferedImage(
//                slices.get(0).getWidth(), slices.get(0).getHeight() * slices.size(),
//                BufferedImage.TYPE_INT_RGB);
//        Graphics g = result.getGraphics();
//        for (int i = 0; i < slices.size(); i++) {
//            BufferedImage bi = slices.get(i);
//            g.drawImage(bi, x, y, null);
//            y += bi.getHeight();
//        }
//
//        url = packtWebDriver.getCurrentUrl();
//        String resultFilename = url.replaceAll("/", "|") + new Date().toString() + ".png";
//        try {
//            ImageIO.write(result, "png", new File("/home/andrii/Downloads/packt/" + resultFilename));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return "it is possible to merge images easy, but i would rather save them all";
    }


    public void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String driver(){
        File driver = new File("C:\\tmp\\chromedriver.exe");
        if(driver.exists()){
            return "C:\\tmp\\chromedriver.exe";
        } else {
            return "/home/andrii/Programs/chromedriver";
        }
    }

    public void getPageAsImage(String url){
        packtWebDriver.get("https://www.packtpub.com/");
    }

}