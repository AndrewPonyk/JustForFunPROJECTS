package com.ap.monitor;

import com.ap.DbUtils;
import com.ap.dao.BetRepo;
import com.ap.dao.BetRepoJdbc;
import com.ap.utils.Constants;
import com.ap.utils.JavaCoreSendMailUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by andrii on 06.01.18.
 */
public class PossibleComebackMonitor implements Runnable {

    private static Long lastMessageDateTime = System.currentTimeMillis()/1000;
    private static final Integer timeoutAfterSendedMessage = 180;
    private static final BetRepo betRepo = new BetRepoJdbc();

    public static void main(String[] args) throws IOException, SQLException, MessagingException {

        String allComebacksFromHistory = betRepo.comebackItemsAndTheirResults(false);

        LinkedList<List<String>> items = new LinkedList<>();
        items.add(new LinkedList<>(Arrays.asList(allComebacksFromHistory)));

        if (!items.isEmpty()) {
            JavaCoreSendMailUtils.sendHtmlTableWithUserData(Constants.BET_EMAIL,
                    "All possible comeback from history", items,
                    Constants.BET_EMAIL, Constants.BET_PASSWORD);
        }
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
