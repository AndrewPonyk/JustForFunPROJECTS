package com.ap.model;


public class BetCoef {
    private Integer votersCount;
    private Double coef;

    @Override
    public String toString() {
        return "BetCoef{" +
                "votersCount=" + votersCount +
                ", coef=" + coef +
                '}';
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
}
