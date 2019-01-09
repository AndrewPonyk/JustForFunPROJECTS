package pdfjoiner;

import java.util.LinkedHashMap;
import java.util.Map;

public class CloneMapApp {
    public static void main(String[] args) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("a", "b");
        System.out.println(map);

        LinkedHashMap<String, String> map2 = new LinkedHashMap<>(map);
        map2.put("c", "c");
        System.out.println(map);
        System.out.println(map2);
    }
}
