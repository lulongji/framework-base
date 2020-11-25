package com.lulj.base.utils.server;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * @author lu
 */
public class JVMUtil {
    private static final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();

    public static String getJVMPID() {
        String pid = getJVMName().split("@")[0];
        return pid;
    }

    public static String getJVMName() {
        String name = runtime.getName();
        return name;
    }

    public static String getJVMPort() {
        String port = System.getProperty("console.port");
        String hostname = IPAddrUtil.getHostname();
        if (port == null || port.trim().length() == 0) {
            port = "880";
        }
        return hostname + ":" + port;
    }

}
