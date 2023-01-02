package com.ap.domain;

import java.time.LocalDateTime;

public class BetItem {
  private String title;
  private Double odd;
  private Double sum;
  private String chose;
  private Boolean status;
  private String finalResult;
  private LocalDateTime created = LocalDateTime.now();

  @Override
  public String toString() {
    return "BetItem{" +
            "title='" + title + '\'' +
            ", odd=" + odd +
            ", sum=" + sum +
            ", chose='" + chose + '\'' +
            ", status=" + status +
            ", finalResult='" + finalResult + '\'' +
            ", created=" + created +
            '}';
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Double getOdd() {
    return odd;
  }

  public void setOdd(Double odd) {
    this.odd = odd;
  }

  public Double getSum() {
    return sum;
  }

  public void setSum(Double sum) {
    this.sum = sum;
  }

  public String getChose() {
    return chose;
  }

  public void setChose(String chose) {
    this.chose = chose;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public String getFinalResult() {
    return finalResult;
  }

  public void setFinalResult(String finalResult) {
    this.finalResult = finalResult;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }
}
