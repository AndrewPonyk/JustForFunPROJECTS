package com.ap.domain;

import java.time.LocalDateTime;

public class SyncItem {
    public String title;
    public String score;
    public Double coef1;
    public Double coef2;
    public String sport;
    public String timeline;
    public String competition;
    public LocalDateTime created;

    public SyncItem(){

    }

    public SyncItem(String title, String score, Double coef1, Double coef2, String sport, String timeline, String competition) {
        this.title = title;
        this.score = score;
        this.coef1 = coef1;
        this.coef2 = coef2;
        this.sport = sport;
        this.timeline = timeline;
        this.competition = competition;
        created = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "SyncItem{" +
                "title='" + title + '\'' +
                ", score='" + score + '\'' +
                ", coef1=" + coef1 +
                ", coef2=" + coef2 +
                ", sport='" + sport + '\'' +
                ", timeline='" + timeline + '\'' +
                ", competition='" + competition + '\'' +
                '}';
    }
}