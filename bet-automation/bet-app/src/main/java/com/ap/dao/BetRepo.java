package com.ap.dao;

import com.ap.model.BetItem;

import java.util.LinkedList;
import java.util.List;

public interface BetRepo {
    List<BetItem> getAll();

    void saveUpdateItems(LinkedList<BetItem> betItems);

    List<BetItem> getStage5Items();

    List<BetItem> getBetsWhereComebackIsPossile();

    void markBetAsCompleted(BetItem bet);

    void updateCurrentBetStatus(Integer winLastBet, Double currentAmount, Double lastLoseBetsSum);

    Integer getLastBetStatus();

    Double getLastLoseBetsSum();

    List<String> getLastBetInfo();

    String stagesCount();

    String comebackItemsAndTheirResults(Boolean onlyCurrDate);

    List<String> getPlayerStagesFromHistory(String title, String sport);

    BetItem getRandomFavourite(int minBetFavItems);
}
