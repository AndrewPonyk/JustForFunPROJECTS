package com.ap.monitor;

import com.ap.Get45StageItems;
import com.ap.utils.JavaCoreSendMailUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by andrii on 25.11.17.
 */
public class Stage345Monitor implements Runnable {
    @Override
    public void run() {
        while (true){
            System.out.println("Monitor 345 stage");
                try {
                    List<List<String>> items = Get45StageItems.get45Items();
                    if(!items.isEmpty()){
                        JavaCoreSendMailUtils.sendHtmlTable("", "stage345 items", items,
                                "", "");
                    }
                    Thread.sleep(75000);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}