package com.ap;

import com.ap.browser.BetBrowser;
import com.ap.monitor.Stage345Monitor;
import com.ap.monitor.Tennis2ndAfterLose1stMonitor;

public class BetPlacingApp {
    public static void main(String[] args) {
        BetBrowser betBrowser = new BetBrowser();

        Thread tennis2ndSetMonitor = new Thread(new Tennis2ndAfterLose1stMonitor());
        tennis2ndSetMonitor.start();
        Thread stage345Monitor = new Thread(new Stage345Monitor());
        stage345Monitor.start();

        betBrowser.start();
    }
}
