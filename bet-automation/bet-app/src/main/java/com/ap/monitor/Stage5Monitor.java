package com.ap.monitor;

import com.ap.Get45StageItems;
import com.ap.utils.Constants;
import com.ap.utils.JavaCoreSendMailUtils;

import java.util.List;

public class Stage5Monitor implements Runnable {
    @Override
    public void run() {
        while (true){
            System.out.println("Monitor 5 stage");
                try {
                    List<List<String>> items = Get45StageItems.get5Items();
                    if(!items.isEmpty()){
                        JavaCoreSendMailUtils.sendHtmlTable(Constants.BET_EMAIL, "Stage 5 items", items,
                                Constants.BET_EMAIL, Constants.BET_PASSWORD);
                    }
                    Thread.sleep(80000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }
}