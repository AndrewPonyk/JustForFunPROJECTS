package com.ap.model;

import java.sql.Date;
import java.util.LinkedList;

public class BetItem {
    private Integer id;
    private String title;
    private String sport;
    private LinkedList<MomentResult> results;
    private String stage;

    private String notes;
    private String link;

    private Date date;
    private Integer finalWinner;

    private String competition;
    private String liveStream;

    public BetItem(String title, String sport, LinkedList<MomentResult> results, String stage, String link, String notes,
                   String competition) {
        this.title = title;
        this.sport = sport;
        this.results = results;
        this.stage = stage;
        this.link = link;
        this.notes = notes;
        this.competition = competition;
    }

    @Override
    public String toString() {
        return "BetItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", sport='" + sport + '\'' +
                ", results=" + results +
                ", stage=" + stage +
                ", date=" + date +
                ", finalWinner=" + finalWinner +
                ", link=" + link +
                '}';
    }

    public static void main(String[] args) {
        System.out.println("");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public LinkedList<MomentResult> getResults() {
        return results;
    }

    public void setResults(LinkedList<MomentResult> results) {
        this.results = results;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getFinalWinner() {
        return finalWinner;
    }

    public void setFinalWinner(Integer finalWinner) {
        this.finalWinner = finalWinner;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public String getLiveStream() {
        return liveStream;
    }

    public void setLiveStream(String liveStream) {
        this.liveStream = liveStream;
    }
}
