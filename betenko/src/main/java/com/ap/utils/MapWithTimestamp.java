package com.ap.utils;

import com.ap.domain.SyncItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class MapWithTimestamp {
    private Map<Pair<String, LocalDateTime>, LinkedList<SyncItem>> data = new TreeMap<>(new PairComparator());

    static ObjectMapper objectMapper;
    private static int MINUTES_TO_BE_CURRENT = 33;
    private static int MINUTES_TO_BE_SCORE_CURRENT = 14;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addKeyDeserializer(Pair.class, new PairKeyDeserializer());
        objectMapper.registerModule(simpleModule);
    }

    public MapWithTimestamp() {
    }

    public MapWithTimestamp(Map<Pair<String, LocalDateTime>, LinkedList<SyncItem>> data) {
        this.data.putAll(data);
    }

    public Map<Pair<String, LocalDateTime>, LinkedList<SyncItem>> getData() {
        return data;
    }

    public void setData(Map<Pair<String, LocalDateTime>, LinkedList<SyncItem>> data) {
        this.data.clear();
        this.data.putAll(data);
    }

    public void put(String key, SyncItem value) {
        Optional<Map.Entry<Pair<String, LocalDateTime>, LinkedList<SyncItem>>> first = data.entrySet().stream().filter(pair -> {
            long between = Math.abs(ChronoUnit.MINUTES.between(pair.getKey().getValue(), value.created));

            if(pair.getValue() != null && !pair.getValue().isEmpty()){
            }
            System.out.println(between);
            return pair.getKey().getKey().equals(key) && between <= MINUTES_TO_BE_CURRENT;
        }).findFirst();

        if (first.isPresent()) {
            if(!first.get().getValue().getLast().score.equalsIgnoreCase(value.score)){
                first.get().getValue().add(value);
            }
        } else {
            LinkedList<SyncItem> values = new LinkedList<>();
            values.add(value);
            //hack, adding in loop can have similar LocaldateTime
            try{Thread.sleep(1);}catch (Exception e){}
            System.out.println("Putting new:" + key + value.created);
            data.put(Pair.of(key, value.created), values);
        }
    }

    public LinkedList<SyncItem> getCurrent(String key) {
        Optional<Map.Entry<Pair<String, LocalDateTime>, LinkedList<SyncItem>>> first = data.entrySet().stream().filter(pair -> {
            long between = Math.abs(ChronoUnit.MINUTES.between(pair.getKey().getValue(), LocalDateTime.now()));
            return pair.getKey().getKey().equals(key) && between <= MINUTES_TO_BE_CURRENT;
        }).findFirst();
        if (first.isPresent()) {
            return first.get().getValue();
        }

        return null;
    }

    public MapWithTimestamp getAllByTwoKeys(String key1, String key2, int interval) {
        Map<Pair<String, LocalDateTime>, LinkedList<SyncItem>> result = data.entrySet().stream().filter(pair -> {
            long between = Math.abs(ChronoUnit.MINUTES.between(pair.getKey().getValue(), LocalDateTime.now()));
            return pair.getKey().getKey().contains(key1) && pair.getKey().getKey().contains(key2) && between <= interval;
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new MapWithTimestamp(result);
    }

    public MapWithTimestamp getLastInInterval(int interval) {
        Map<Pair<String, LocalDateTime>, LinkedList<SyncItem>> result = data.entrySet().stream().filter(pair -> {
            long between = Math.abs(ChronoUnit.MINUTES.between(pair.getKey().getValue(), LocalDateTime.now()));
            return  between <= interval;
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new MapWithTimestamp(result);
    }

    public Map<Pair<String, LocalDateTime>, LinkedList<SyncItem>> getAllByKey(String key) {
        return null;
    }

    public static MapWithTimestamp parseJson(String json) throws JsonProcessingException {

        return objectMapper.readValue(json, MapWithTimestamp.class);
    }

    @Override
    public String toString() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("cant serialize");
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        MapWithTimestamp mapWithTimestamp = new MapWithTimestamp();

        SyncItem syncItem1 =
                new SyncItem("(KPEBETKA FC (KFC) - Black star (BSR)", "2-0", 1.2, 5.0, "football", "233", "esoccer");
        SyncItem syncItem2 =
                new SyncItem("abcd", "2-0", 1.2, 5.0, "football", "233", "esoccer");
        SyncItem syncItem3 =
                new SyncItem("abc", "2-0", 1.3, 4.0, "football", "233", "esoccer");
        SyncItem syncItem4 =
                new SyncItem("abc", "2-1", 1.3, 4.0, "football", "233", "esoccer");
        syncItem4.created = LocalDateTime.now().plusMinutes(43);

        mapWithTimestamp.put(syncItem1.title, syncItem1);
        mapWithTimestamp.put(syncItem2.title, syncItem2);
        mapWithTimestamp.put(syncItem3.title, syncItem3);
        mapWithTimestamp.put(syncItem4.title, syncItem4);

        String str = mapWithTimestamp.toString();
        mapWithTimestamp = MapWithTimestamp.parseJson(str);
        System.out.println(mapWithTimestamp);
        System.out.println(mapWithTimestamp.getData().size());
    }

    public static class PairKeyDeserializer extends KeyDeserializer {
        @Override
        public Object deserializeKey(String s, DeserializationContext deserializationContext) throws IOException {
            s = StringUtils.strip(s, "()");
            //can be comma inside the title
            int lastIndexOfComma = s.lastIndexOf(",");
            return Pair.of(s.substring(0,lastIndexOfComma), LocalDateTime.parse(s.substring(lastIndexOfComma+1)));
        }

    }

    public static class PairComparator implements Comparator<Pair<String, LocalDateTime>> {
        @Override
        public int compare(Pair<String, LocalDateTime> o1, Pair<String, LocalDateTime> o2) {
            int compareUsingDates = -1 * o1.getValue().compareTo(o2.getValue());
            if(compareUsingDates == 0){
                return o1.getKey().compareTo(o2.getKey());
            }

            return compareUsingDates;
        }
    }

}
