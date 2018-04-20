import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Test {
    public static void main(String[] args) {
        System.out.println("testing features");

        MutablePair<Integer, Integer> pair = new MutablePair<>(0, 0);
        pair.setLeft(10);
        pair.setRight(100);
        System.out.println(pair);
        Integer i = (Integer) null;


        Lists.newArrayList(1,2,3);
    }


}
