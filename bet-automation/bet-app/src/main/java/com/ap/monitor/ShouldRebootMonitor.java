package com.ap.monitor;

import com.ap.utils.Constants;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by andrii on 13.01.18.
 */
public class ShouldRebootMonitor implements Runnable {
    private int connectionAttempts = 0;

    @Override
    public void run() {
        while (true){
            System.err.println("Monitor system alive and reboot if needed");
            try {
                String status = readStatus();
                if("true".equals(status)){
                    rebootPC();
                }
                Thread.sleep(300000);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void rebootPC(){
        try {
            Runtime.getRuntime().exec("reboot");
        } catch (IOException e) {
            System.out.println("Can not reboot system");
        }
    }

    public String readStatus(){
        URL url = null;
        InputStream is = null;
        String result = "";
        try {
            url = new URL(Constants.STATUS_URL);
            URLConnection conn = url.openConnection();
            is = conn.getInputStream();
            List<String> strings = IOUtils.readLines(is, "UTF-8");
            result = strings.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        ShouldRebootMonitor shouldRebootMonitor = new ShouldRebootMonitor();
        String s = shouldRebootMonitor.readStatus();
        System.out.println(s);
    }


}
