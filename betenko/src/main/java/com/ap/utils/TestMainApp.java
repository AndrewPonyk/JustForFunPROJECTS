package com.ap.utils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class TestMainApp {
    public static void main(String[] args) {
        LinkedList<String> test = new LinkedList<String>();
        test.add("a");
        test.add("b");

        Map<Integer, String> map = test.stream().collect(toMap(key -> key.hashCode(), value -> value));
        System.out.println(map);

        LinkedHashMap<String, String> mapWIthOrder = new LinkedHashMap<>();
        mapWIthOrder.put("hello", "1");
        mapWIthOrder.put("world", "2");
        mapWIthOrder.put("and", "3");

        System.out.println(mapWIthOrder);
        AtomicInteger counter = new AtomicInteger(0);
        Map<String, Map.Entry<String, String>> mappedMapWithOrder = mapWIthOrder.entrySet().stream().collect(toMap(key -> counter.incrementAndGet() + "", value -> value));
        System.out.println(mappedMapWithOrder);

    }
}
