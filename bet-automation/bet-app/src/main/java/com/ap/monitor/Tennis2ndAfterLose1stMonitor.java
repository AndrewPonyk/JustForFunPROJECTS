package com.ap.monitor;

import com.ap._3FavoriteWinAfterLooseSet;
import com.ap.utils.Constants;
import com.ap.utils.JavaCoreSendMailUtils;

import java.util.LinkedList;
import java.util.List;

public class Tennis2ndAfterLose1stMonitor implements Runnable{

    @Override
    public void run() {
        while (true){
            System.out.println("Monitor 2nd set after lose 1st");

            try {

                LinkedList<List<String>> mailItems = _3FavoriteWinAfterLooseSet.processResultSet();
                if(!mailItems.isEmpty()){
                    JavaCoreSendMailUtils.sendHtmlTableWithUserData(Constants.BET_EMAIL, "SET 2 TENNIS notification", mailItems,
                            Constants.BET_EMAIL, Constants.BET_PASSWORD);
                }
                Thread.sleep(50000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
