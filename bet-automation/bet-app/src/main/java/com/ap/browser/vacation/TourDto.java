package com.ap.browser.vacation;


import java.util.ArrayList;
import java.util.List;

public class TourDto {
    private String nameAndCity;
    private String departureAndCity;
    private String daysCount;
    private String food;
    private String feedbacks;
    private String price;
    private String desc;
    private String link;
    @Override
    public String toString() {
        return "TourDto{" +
                "nameAndCity='" + nameAndCity + '\'' +
                ", departureAndCity='" + departureAndCity + '\'' +
                ", daysCount='" + daysCount + '\'' +
                ", food='" + food + '\'' +
                ", feedbacks='" + feedbacks + '\'' +
                ", price='" + price + '\'' +
                ", desc='" + desc + '\'' +
                 link+
                '}';
    }


    public List<String> toList() {
        List<String> result = new ArrayList<>();
        result.add(nameAndCity);
        result.add(departureAndCity);
        result.add(daysCount);
        result.add(food);
        result.add(feedbacks);
        result.add(price);
        result.add(desc);
        result.add(link);
        return result;
    }

    public String getNameAndCity() {
        return nameAndCity;
    }

    public void setNameAndCity(String nameAndCity) {
        this.nameAndCity = nameAndCity;
    }

    public String getDepartureAndCity() {
        return departureAndCity;
    }

    public void setDepartureAndCity(String departureAndCity) {
        this.departureAndCity = departureAndCity;
    }

    public String getDaysCount() {
        return daysCount;
    }

    public void setDaysCount(String daysCount) {
        this.daysCount = daysCount;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(String feedbacks) {
        this.feedbacks = feedbacks;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
