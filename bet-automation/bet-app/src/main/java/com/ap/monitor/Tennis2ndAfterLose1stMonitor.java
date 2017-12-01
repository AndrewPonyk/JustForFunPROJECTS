package com.ap.monitor;

import com.ap._3FavoriteWinAfterLooseSet;
import com.ap.utils.JavaCoreSendMailUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by andrii on 23.11.17.
 */
public class Tennis2ndAfterLose1stMonitor implements Runnable{

    @Override
    public void run() {
        while (true){
            System.out.println("Monitor 2nd set after lose 1st");

            try {

                List<List<String>> mailItems = _3FavoriteWinAfterLooseSet.processResultSet();
                if(!mailItems.isEmpty()){
                    JavaCoreSendMailUtils.sendHtmlTable("", "SET 2 TENNIS notification", mailItems,
                            "", "");
                }
                Thread.sleep(50000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
