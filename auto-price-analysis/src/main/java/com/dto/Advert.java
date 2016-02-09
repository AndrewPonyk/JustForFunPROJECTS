package com.dto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by andrii on 25.01.16.
 */
public class Advert {
    public String name;
    public String city;
    public Integer year;
    public String description;

    public Integer price;
    public Integer mileage;
    private String hash;
    public String autoriaId;
    public String imageUrl;
    public boolean imageSaved;

    public String model;
    public Integer averagePriceOnThisDate;


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
    
    public Advert setAutoriaId(String autoriaId) {
        this.autoriaId = autoriaId;
        return this;
    }

    public Advert setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
        return this;
    }

    public Advert setAveragePriceOnThisDate(Integer price){
        this.averagePriceOnThisDate = price;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Advert advert = (Advert) o;
        return this.getHash().equals(advert.getHash());
    }

    @Override
    public int hashCode() {
//        int result = name.hashCode();
//        result = 31 * result + (city != null ? city.hashCode() : 0);
//        result = 31 * result + (year != null ? year.hashCode() : 0);
//        result = 31 * result + (description != null ? description.hashCode() : 0);
//        return result;
        return this.getHash().hashCode();
    }

    public Advert setPrice(Integer price) {
        this.price = price;
        return this;

    }

    public Advert setMileage(Integer mileage) {
        this.mileage = mileage;
        return this;
    }

    public Advert setModel(String model) {
        this.model = model;
        return this;
    }

    @Override
    public String toString() {
        return String.format("{%s}[%s][%s][%s][%s][%s][autoriaId=%s][%s]<ImageSaved=%s>",
                getHash(), name, city, price, year, mileage, autoriaId, description, imageSaved);
    }

    public String getHash(){
        if(this.hash != null){
            return this.hash;
        }

        String descriptionForHash = this.description.length() > 15 ? this.description.substring(0, 15) : this.description;
        String original = this.name + this.city + this.year + this.mileage + descriptionForHash;
        StringBuffer sb = new StringBuffer();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(original.getBytes());
            byte[] digest = md.digest();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        this.hash = sb.toString().substring(0,5); // use first symbols as id of advert
        return hash;
    }

}