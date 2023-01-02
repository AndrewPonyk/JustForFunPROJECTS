package com.ap.domain;

import java.util.LinkedList;

public class Player {
    private String name;
    private Double sum;
    private LinkedList<BetItem> betItems;

    public Player() {
    }

    public Player(String name, Double sum, LinkedList<BetItem> betItems) {
        this.name = name;
        this.sum = sum;
        this.betItems = betItems;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", sum=" + sum +
                ", betItems=" + betItems +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public LinkedList<BetItem> getBetItems() {
        return betItems;
    }

    public void setBetItems(LinkedList<BetItem> betItems) {
        this.betItems = betItems;
    }
}
