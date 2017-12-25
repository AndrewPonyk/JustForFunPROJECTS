package com.ap.dao;

import com.ap.model.BetItem;

import java.util.LinkedList;
import java.util.List;

public interface BetRepo {
    List<BetItem> getAll();

    void saveUpdateItems(LinkedList<BetItem> betItems);

    List<BetItem> getStage5Items();

    void markBetAsCompleted(BetItem bet);

    void updateCurrentBetStatus(Integer winLastBet, Double currentAmount);

    Integer getLastBetStatus();

    List<String> getLastBetInfo();

    String stagesCount();

    List<String> getPlayerStagesFromHistory(String title, String sport);
}
