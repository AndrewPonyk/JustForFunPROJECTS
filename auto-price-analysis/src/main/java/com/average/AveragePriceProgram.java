package com.average;

import com.services.FileSystemService;
import com.services.AverageService;
import com.services.SeleniumService;
import com.services.FileSystemServiceImpl;
import com.services.SeleniumServiceImpl;
import com.services.AverageServiceImpl;
import com.dto.Advert;

import java.util.List;

/**
 * Created by andrii on 24.01.16.
 */
public class AveragePriceProgram {
    static AverageService averageService = new AverageServiceImpl();
    static FileSystemService fileSystemService = new FileSystemServiceImpl();
    static SeleniumService seleniumService = new SeleniumServiceImpl();

    public static void main(String[] args) {
        System.out.println("Average price of cars");

        fileSystemService.getCarsAndTheirUrls().forEach((carName, carUrl) -> {
            System.out.println(carName + "|" + carUrl);
            List<Advert> allCars = seleniumService.getAllCars(carUrl);
            allCars.forEach(car->{
                System.out.println(car);
            });
            System.out.println("=====================");
        });

    }

}
