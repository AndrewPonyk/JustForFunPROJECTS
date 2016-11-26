package lohika;

import java.util.Arrays;

public class StringRegexIteratorApp {
    public static void main(String[] args) {

        String data = "adfasdf999asdfag3443abc";
        StringContainter containter = new StringContainter(data, "\\d+");
        Iterator iterator = containter.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}