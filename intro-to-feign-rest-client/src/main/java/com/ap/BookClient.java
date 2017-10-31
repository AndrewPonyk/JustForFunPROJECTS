package com.ap;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

/**
 * Created by andrii on 19.10.17.
 */
public interface BookClient {
    @RequestLine("GET /{isbn}")
    BookResource findByIsbn(@Param("isbn") String isbn);

    @RequestLine("GET")
    List<BookResource> findAll();

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    void create(Book book);
}
