package com.ap;

import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

/**
 * Created by andrii on 19.10.17.
 */
public class TestApp {
    public static void main(String[] args) {
        BookClient bookClient = Feign.builder()
                .client(new OkHttpClient())
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .logger(new Slf4jLogger(BookClient.class))
                .logLevel(Logger.Level.FULL)
                .target(BookClient.class, "http://localhost:8080/books");

        BookResource result = bookClient.findByIsbn("100");
        System.out.println(result);
    }
}
