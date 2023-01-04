package packtSaver.video;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Testtt {
    public static void main(String[] args) throws IOException {
        System.out.println("test");
        final List<String> collect = Files.walk(Paths.get("F:\\tmp\\packt\\video\\Java Multithreading and Parallel Programming Masterclass [Video]--5gb-NEED-OPTIM"))
                .filter(Files::isRegularFile)
                .map(p -> p.toString())
                .collect(Collectors.toList());
        System.out.println(collect);
    }
}
