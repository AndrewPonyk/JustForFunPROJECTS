package com.ap.domain;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Grouping {
    public static void main(String[] args) {

        Book book1 = Book.builder()
                .id(1)
                .authors(Arrays.asList("Author1", "Author2"))
                .build();
        Book book2 = Book.builder()
                .id(2)
                .authors(Arrays.asList("Author2", "Author3"))
                .build();
        Book book3 = Book.builder()
                .id(3)
                .authors(Arrays.asList("Author1", "Author3"))
                .build();
        List<Book> books = Arrays.asList(book1, book2, book3);
        Map<String, List<Book>> authorBookMap;

        Map<String, List<Book>> collect = books.stream()
                .flatMap(book -> book.getAuthors().stream()).distinct()
                .map(author ->
                        Pair.of(author, books.stream().filter(book -> book.authors.contains(author))
                                .collect(Collectors.toList())))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        System.out.println(collect);


    }

    @Data
    @Builder
    public  static class Book {
        int id;
        List<String> authors;
    }
}
