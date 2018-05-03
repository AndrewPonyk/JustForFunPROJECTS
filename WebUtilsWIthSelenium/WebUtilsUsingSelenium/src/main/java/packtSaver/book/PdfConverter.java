package packtSaver.book;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by andrii on 15.07.17.
 */
public class PdfConverter {

    public void createPdfFromImages(){

    }

    public static void main(String[] args) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("C:\\tmp\\packt\\javacreate.pdf"));
            document.open();

            Paragraph p = new Paragraph();
            p.add("This is my paragraph");
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Paragraph p2 = new Paragraph();
            Font f = new Font();
            f.setStyle(Font.BOLD);
            f.setSize(14);
            p2.setFont(f);
            p2.add("This is bold right algnment paragraph");
            p2.setAlignment(Element.ALIGN_RIGHT);
            document.add(p2);

            Image image = new Jpeg(new URL("file:C:\\tmp\\packt\\Example.jpg"));
            document.add(image);
            File ff = new File("C:\\tmp\\packt\\Example.jpg");
            System.out.println(ff.toURI().toURL());

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
