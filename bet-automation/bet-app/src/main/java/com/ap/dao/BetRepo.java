package com.ap.dao;

import com.ap.model.BetItem;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.SQLException;
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

    String comebackItemsAndTheirResultsAsHtml(Boolean onlyCurrDate);

    LinkedList<Pair<Integer, BetItem>> getAllComebackItemsFromHistory(int count) throws SQLException;

    List<String> getPlayerStagesFromHistory(String title, String sport);

    BetItem getRandomFavourite(int minBetFavItems);
}
