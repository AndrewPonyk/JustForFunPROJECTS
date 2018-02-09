package com.ap.monitor;

import com.ap.DbUtils;
import com.ap.utils.Constants;
import com.ap.utils.JavaCoreSendMailUtils;

import java.util.LinkedList;
import java.util.List;

public class Stage12345Monitor implements Runnable {
    @Override
    public void run() {
        while (true) {
            System.out.println("Monitor 12345 stage");
            try {
                LinkedList<List<String>> items = DbUtils.get12345Items();

                if (!items.isEmpty()) {
                    JavaCoreSendMailUtils.sendHtmlTableWithUserData(Constants.BET_EMAIL, "Stage 12345 items", items,
                            Constants.BET_EMAIL, Constants.BET_PASSWORD);
                }
                Thread.sleep(200000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}