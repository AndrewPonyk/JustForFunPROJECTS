package com.ap.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SystemUtils {

    private static String pcname = null;

    public static String getPcName(){
        if(pcname != null){
            return pcname;
        }
        String result = "";
        try {
            result = InetAddress.getLocalHost().getHostName();
            pcname = result;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return result;
    }
}
