package com.ap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfiguration {
    @Bean
    public ExecutorService creatThreadPoolExecutor(){
        ExecutorService executorService = Executors.newFixedThreadPool(15);
        return executorService;
    }
}
