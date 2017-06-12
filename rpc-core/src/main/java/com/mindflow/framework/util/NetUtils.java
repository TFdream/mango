package com.mindflow.framework.util;

import java.net.InetSocketAddress;

/**
 * @author Ricky Fung
 */
public class NetUtils {

    public static InetSocketAddress parseSocketAddress(final String addr) {
        String[] arr = addr.split(":");
        InetSocketAddress isa = new InetSocketAddress(arr[0], Integer.parseInt(arr[1]));
        return isa;
    }
}
