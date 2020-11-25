package com.lulj.base.utils.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author lu
 */
public class NetworkUtil {

    /**
     * 获取机器名称
     *
     * @return
     */
    public static String getHostName() {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }

}
