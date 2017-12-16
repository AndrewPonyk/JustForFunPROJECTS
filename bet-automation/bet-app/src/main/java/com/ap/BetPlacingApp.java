package com.ap;

import com.ap.browser.BetBrowser;
import com.ap.monitor.CurrentBetStatusMonitor;
import com.ap.monitor.Stage12345Monitor;
import com.ap.monitor.Stage5Monitor;
import com.ap.monitor.Tennis2ndAfterLose1stMonitor;

public class BetPlacingApp {
    public static void main(String[] args) {
        BetBrowser betBrowser = new BetBrowser();

        Thread tennis2ndSetMonitor = new Thread(new Tennis2ndAfterLose1stMonitor());
        tennis2ndSetMonitor.start();
        Thread stage345Monitor = new Thread(new Stage5Monitor());
        stage345Monitor.start();
        Thread stage12345Monitor = new Thread(new Stage12345Monitor());
        stage12345Monitor.start();
        Thread currentStatusMonitor = new Thread(new CurrentBetStatusMonitor());
        currentStatusMonitor.start();

        betBrowser.start();
    }
}
