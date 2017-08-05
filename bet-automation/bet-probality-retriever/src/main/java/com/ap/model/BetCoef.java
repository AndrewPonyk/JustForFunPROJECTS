package com.ap.model;


public class BetCoef {
    private Integer votersCount;
    private Double coef;

    private Double bet365Coef;

    @Override
    public String toString() {
        return "BetCoef{" +
                "votersCount=" + votersCount +
                ", coef=" + coef +
                ", bet365Coef=" + bet365Coef +
                '}';
    }

    public BetCoef() {
    }

    public BetCoef(Integer votersCount, Double coef) {
        this.votersCount = votersCount;
        this.coef = coef;
    }

    public Integer getVotersCount() {
        return votersCount;
    }

    public void setVotersCount(Integer votersCount) {
        this.votersCount = votersCount;
    }

    public Double getCoef() {
        return coef;
    }

    public void setCoef(Double coef) {
        this.coef = coef;
    }

    public Double getBet365Coef() {
        return bet365Coef;
    }

    public void setBet365Coef(Double bet365Coef) {
        this.bet365Coef = bet365Coef;
    }

}
