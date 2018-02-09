package com.ap.monitor;

import com.ap.DbUtils;
import com.ap.utils.Constants;
import com.ap.utils.JavaCoreSendMailUtils;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by andrii on 06.01.18.
 */
public class PossibleComebackMonitor implements Runnable {

    private static Long lastMessageDateTime = System.currentTimeMillis()/1000;
    private static final Integer timeoutAfterSendedMessage = 180;

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis()/1000);
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Try to findPossible comeback items");
            try {
                LinkedList<List<String>> items = DbUtils.getItemsWithPossibleComeback();
                long currentTime = System.currentTimeMillis() / 1000;

                if (!items.isEmpty() && (currentTime-lastMessageDateTime > timeoutAfterSendedMessage)) {
                    JavaCoreSendMailUtils.sendHtmlTableWithUserData(Constants.BET_EMAIL,
                            "Possible Comeback items", items,
                            Constants.BET_EMAIL, Constants.BET_PASSWORD);
                    lastMessageDateTime = currentTime;
                }

                int minute = LocalDateTime.now().getMinute();
                int second = LocalDateTime.now().getSecond();
                if(minute % 30 == 0 && second > 29){
                    LinkedList<List<String>> allcurrentItems =
                            DbUtils.getAllCurrent_Except1stage();
                    JavaCoreSendMailUtils.sendHtmlTableWithUserData(Constants.BET_EMAIL,
                            "System notification",
                            allcurrentItems,
                            Constants.BET_EMAIL, Constants.BET_PASSWORD);
                }
                Thread.sleep(25000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
