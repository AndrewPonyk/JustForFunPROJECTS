import com.google.common.collect.Lists;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


        ArrayList<Integer> integers = Lists.newArrayList(1, 2, 3);
        ArrayList<Integer> integers1 = Lists.newArrayList(11, 22, 44);
        Map<Long, List<Integer>> map = new HashMap<>();
        map.put(1L, integers);
        map.put(2L, integers1);

        map.values().stream()
                .flatMap(list -> ListUtils.partition(list, 2).stream())
                .map(batches -> batches.stream().map(batch -> {
                    return batch;
                    //return repository.save(batch);
                }))
                .flatMap(e -> e)
                .collect(Collectors.toList());

        List<List<Integer>> collect = map.values().stream()
                .flatMap(list -> ListUtils.partition(list, 2).stream()).collect(Collectors.toList());
        System.out.println(collect);
    }
}
