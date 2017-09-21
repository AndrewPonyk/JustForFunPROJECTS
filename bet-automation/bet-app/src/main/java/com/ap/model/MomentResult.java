package com.ap.model;

import java.time.LocalDateTime;

public class MomentResult {
    private Double coef1;
    private Double coef2;

    private String result;

    public MomentResult() {
    }

    public MomentResult(Double coef1, Double coef2, String result) {
        this.coef1 = coef1;
        this.coef2 = coef2;
        this.result = result;
    }

    @Override
    public String toString() {
        return "MomentResult{" +
                "coef1=" + coef1 +
                ", coef2=" + coef2 +
                ", result='" + result + '\'' +
                //", time=" + time +
                '}';
    }

    public Double getCoef1() {
        return coef1;
    }

    public void setCoef1(Double coef1) {
        this.coef1 = coef1;
    }

    public Double getCoef2() {
        return coef2;
    }

    public void setCoef2(Double coef2) {
        this.coef2 = coef2;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


}
