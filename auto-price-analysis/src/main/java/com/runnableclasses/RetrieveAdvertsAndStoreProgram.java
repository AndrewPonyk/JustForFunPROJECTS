package com.runnableclasses;

import com.services.*;
import com.dto.Advert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrii on 24.01.16.
 */
public class RetrieveAdvertsAndStoreProgram {
    static AverageService averageService = new AverageServiceImpl();
    static FileSystemService fileSystemService = new FileSystemServiceImpl();
    static SeleniumService seleniumService = new SeleniumServiceImpl();
    static CarDAO carDAO = new SQLLiteCarDAO();


    public static void main(String[] args) {
        System.out.println("Average price of cars");
        final List<Advert> allCars = new ArrayList<>();

        fileSystemService.getCarsAndTheirUrls().forEach((carName, carUrl) -> {
            System.out.println(carName + "|" + carUrl);
            List<Advert> allCarsOfSomeModel = seleniumService.getAllCars(carUrl, carName);
            allCarsOfSomeModel.forEach(car -> {
                FileSystemServiceImpl.saveImage(car, car.imageUrl);
                car.imageSaved = true;
                System.out.println(car);
            });
            allCars.addAll(allCarsOfSomeModel);
            System.out.println("=====================");
        });


        carDAO.saveCars(allCars);

    }

}
