package com.ap;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Hello world!
 *
 */
public class GetPCNameExampleApp
{
    public static void main( String[] args ) throws UnknownHostException {
        System.out.println(InetAddress.getLocalHost().getHostName());
    }
}
