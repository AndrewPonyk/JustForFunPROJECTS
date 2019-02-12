package botsfinder.porohs;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PorohsApp {
    public static void main(String[] args) throws IOException {
        System.out.println("Find pbots");
        InputStream stream = PorohsApp.class.getClassLoader().getResourceAsStream("bots-keywords/1.txt");
        List<String> s = IOUtils.readLines(stream);
        System.out.println(s);
        System.out.println(LocalDateTime.now().getHour());
    }
}
