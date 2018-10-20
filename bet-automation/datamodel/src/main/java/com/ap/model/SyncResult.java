package com.ap.model;

public class SyncResult {
    private String player1;
    private String player2;
    private String result;

    public SyncResult(String player1, String player2, String result) {
        this.player1 = player1;
        this.player2 = player2;
        this.result = result;
    }

    @Override
    public String toString() {
        return "SyncResult{" +
                "player1='" + player1 + '\'' +
                ", player2='" + player2 + '\'' +
                ", result='" + result + '\'' +
                '}';
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}