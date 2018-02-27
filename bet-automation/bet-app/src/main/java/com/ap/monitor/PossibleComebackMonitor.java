package com.ap.monitor;

import com.ap.DbUtils;
import com.ap.dao.BetRepo;
import com.ap.dao.BetRepoJdbc;
import com.ap.model.BetItem;
import com.ap.model.MomentResult;
import com.ap.utils.Constants;
import com.ap.utils.JavaCoreSendMailUtils;
import com.ap.utils.ValidationUtils;
import com.ap.wincheckers.WinChecker;
import com.ap.wincheckers.WinCheckerProvider;
import org.apache.commons.lang3.tuple.Pair;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by andrii on 06.01.18.
 */
public class PossibleComebackMonitor implements Runnable {
    // Color reset
    public static final String RESET = "\033[0m";  // Text Reset
    // Regular Colors
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN

    private static Long lastMessageDateTime = System.currentTimeMillis()/1000;
    private static final Integer timeoutAfterSendedMessage = 180;
    private static final BetRepo betRepo = new BetRepoJdbc();

    public static void main(String[] args) throws IOException, SQLException, MessagingException {

//        String allComebacksFromHistory = betRepo.comebackItemsAndTheirResultsAsHtml(false);
//
//        LinkedList<List<String>> items = new LinkedList<>();
//        items.add(new LinkedList<>(Arrays.asList(allComebacksFromHistory)));
//
//        if (!items.isEmpty()) {
//            JavaCoreSendMailUtils.sendHtmlTableWithUserData(Constants.BET_EMAIL,
//                    "All possible comeback from history", items,
//                    Constants.BET_EMAIL, Constants.BET_PASSWORD);
//        }

        //showInvalidBets();

        showItemsWith132_148AndTheirResults();
    }


    public static void showInvalidBets(){
        LinkedList<Pair<Integer, BetItem>> itemsFromHistory = betRepo.getItemsFromHistory(2000);
        itemsFromHistory.forEach(item->{
            LinkedList<MomentResult> results = item.getRight().getResults();
            boolean result = ValidationUtils.checkFirst5Results(results);
            if(!result){
                System.out.println(item.getRight().getTitle() + " " + item.getRight().getNotes() + " "
                + item.getRight().getDate());
            }
        });
    }

    public static void showItemsWith132_148AndTheirResults(){
        final AtomicBoolean flag = new AtomicBoolean(true);
        LinkedList<Pair<Integer, BetItem>> itemsFromHistory = betRepo.getItemsFromHistory(2000);
        itemsFromHistory.forEach(item->{
            BetItem betItem = item.getRight();
            int favorite = betItem.getStage().contains("r1") ? 1 : 2;
            WinChecker winChecker = WinCheckerProvider.getWinChecker(betItem.getSport());
            if(favorite == 1){
                for (MomentResult momentResult: betItem.getResults()){
                    if(momentResult.getCoef1()>=4.8 && momentResult.getCoef1()<=5.3){
                        if(winChecker != null){
                            int winner = winChecker.getWinner(betItem.getResults().getLast().getResult());
                            if(winner == favorite){
                                System.out.println(GREEN + betItem.getTitle() + ": " + "[" + betItem.getDate() + "]" + "[" + betItem.getDate() + "]"
                                        + betItem.getResults().getLast().getResult()
                                        + RESET );
                                if(flag.get()){System.out.println(); }flag.set(!flag.get());
                                break;
                            } else if(winner != -1 && winner == 2){
                                System.out.println(RED + betItem.getTitle() + ": " + "[" + betItem.getDate() + "]"
                                        + betItem.getResults().getLast().getResult()
                                        + RESET );
                                if(flag.get()){System.out.println(); }flag.set(!flag.get());
                                break;
                            } else if(winner == -1){
                                System.out.println(betItem.getTitle() + ": " + "[" + betItem.getDate() + "]"
                                        + betItem.getResults().getLast().getResult()
                                        + RESET );
                                if(flag.get()){System.out.println(); }flag.set(!flag.get());
                                break;
                            }
                        }


                    }
                }
            }


            if(favorite == 2){
                for (MomentResult momentResult: betItem.getResults()){
                    if(momentResult.getCoef2()>=4.8 && momentResult.getCoef2()<=5.3){
                        if(winChecker != null){
                            int winner = winChecker.getWinner(betItem.getResults().getLast().getResult());
                            if(winner == favorite){
                                System.out.println(GREEN + betItem.getTitle() + ": " + "[" + betItem.getDate() + "]"
                                        + betItem.getResults().getLast().getResult()
                                        + RESET);
                                if(flag.get()){System.out.println(); }flag.set(!flag.get());
                                break;
                            } else if(winner != -1 && winner == 1){
                                System.out.println(RED + betItem.getTitle() + ": " + "[" + betItem.getDate() + "]"
                                        + betItem.getResults().getLast().getResult()
                                        + RESET);
                                if(flag.get()){
                                    System.out.println();
                                }flag.set(!flag.get());
                                break;
                            } else if(winner == -1){
                                System.out.println(betItem.getTitle() + ": " + "[" + betItem.getDate() + "]"
                                        + betItem.getResults().getLast().getResult()
                                        + RESET );
                                if(flag.get()){System.out.println("dd"); }flag.set(!flag.get());
                                break;
                            }
                        }

                    }
                }
            }

        });
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
