package com.ap;

import com.ap.browser.BetBrowser;
import com.ap.monitor.*;

public class BetPlacingApp {
    public static void main(String[] args) {
        BetBrowser betBrowser = new BetBrowser();

        // disable set 2 monitor
        //Thread tennis2ndSetMonitor = new Thread(new Tennis2ndAfterLose1stMonitor());
        //tennis2ndSetMonitor.start();
        //Thread stage345Monitor = new Thread(new Stage5Monitor());
        //stage345Monitor.start();
        //Thread stage12345Monitor = new Thread(new Stage12345Monitor());
        //stage12345Monitor.start();

        Thread currentStatusMonitor = new Thread(new CurrentBetStatusMonitor());
        currentStatusMonitor.start();
        Thread possibleComebackMonitor = new Thread(new PossibleComebackMonitor());
        possibleComebackMonitor.start();

        betBrowser.start();
    }
}
