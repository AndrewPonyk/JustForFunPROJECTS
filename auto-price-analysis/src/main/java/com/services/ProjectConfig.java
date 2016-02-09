package com.services;

/**
 * Created by andrii on 01.02.16.
 */
public class ProjectConfig {
    public static String projectDir = System.getProperty("user.dir");
    public static String folderWithImages = projectDir + "/dataFolder/images/";
    public static String fileWithCars = projectDir + "/dataFolder/carsWithFilters.txt";
    public static String carsDBFile = projectDir + "/dataFolder/cars.db";
}
