package com.dto;

/**
 * Created by andrii on 25.01.16.
 */
public class Advert {
    public String name;
    public String city;
    public Integer year;
    public String description;

    public String price;
    public Integer mileage;

    public Advert setName(String name) {
        this.name = name;
        return this;
    }

    public Advert setCity(String city) {
        this.city = city;
        return this;
    }

    public Advert setYear(Integer year) {
        this.year = year;
        return this;
    }

    public Advert setDescription(String description) {
        this.description = description;
        return this;
    }

    public Advert setPrice(String price) {
        this.price = price;
        return this;
    }

    public Advert setMileage(Integer mileage) {
        this.mileage = mileage;
        return this;
    }

    @Override
    public String toString() {
        return name + ":" + city + ":" + price;
    }
}
