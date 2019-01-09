package pdfjoiner;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class MapComparatorApp {

    public static void main(String[] args) {
        System.out.println("test comparator");
        LinkedHashMap<String, Pair<Integer, Integer>> original = new LinkedHashMap<>();
        original.put("volleybal", Pair.of(4,34));
        original.put("tennis", Pair.of(4,3));

        System.out.println(original);

        ValueComparator vcp = new ValueComparator(original);
        TreeMap<String, Pair<Integer, Integer>> sorted = new TreeMap<>(vcp);
        sorted.putAll(original);

        System.out.println(sorted);
    }

    public static class ValueComparator implements Comparator<String> {

        @Override
        public boolean equals(Object obj) {
            System.out.println("here");
            return super.equals(obj);
        }

        final Map<String, Pair<Integer, Integer>> base;

        public ValueComparator(Map<String, Pair<Integer, Integer>> base) {
            this.base = base;
        }

        @Override
        public int compare(String o1, String o2) {
            int result = -1*  Double.compare((double)base.get(o1).getKey()/base.get(o1).getValue(),
                    (double)base.get(o2).getKey()/base.get(o2).getValue());
            if(result == 0){
                result = -1;
            }
            return  result;
        }


    }
}
