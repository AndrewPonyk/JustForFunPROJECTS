package com.ap.utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.net.URL;

/**
 * Created by andrii on 24.02.18.
 */
public class RestUtils {

    public static void sendStatus(String status){
        try {
            String url = "";
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "text/html");

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(status);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            System.out.println(responseCode);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        sendStatus("<b>123</b>");
    }
}
