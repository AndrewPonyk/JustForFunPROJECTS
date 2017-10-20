package com.ap.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class MomentResult {
    private Double coef1;
    private Double coef2;

    @JsonProperty("resultTime")
    private String resultTime;

    private String result;

    public MomentResult() {
    }

    public MomentResult(Double coef1, Double coef2, String result, String localDateTime) {
        this.coef1 = coef1;
        this.coef2 = coef2;
        this.result = result;
        this.resultTime = localDateTime;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MomentResult that = (MomentResult) o;

        if (coef1 != null ? !coef1.equals(that.coef1) : that.coef1 != null) return false;
        if (coef2 != null ? !coef2.equals(that.coef2) : that.coef2 != null) return false;
        return result != null ? result.equals(that.result) : that.result == null;
    }

    @Override
    public int hashCode() {
        int result1 = coef1 != null ? coef1.hashCode() : 0;
        result1 = 31 * result1 + (coef2 != null ? coef2.hashCode() : 0);
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        return result1;
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

    public static void main(String[] args) {
        LocalDateTime l = LocalDateTime.now();
        System.out.println(l.toString());
    }

    public String getResultTime() {
        return resultTime;
    }

    public void setResultTime(String resultTime) {
        this.resultTime = resultTime;
    }
}
