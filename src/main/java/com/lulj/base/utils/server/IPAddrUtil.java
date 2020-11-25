package com.lulj.base.utils.server;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author lu
 */
public final class IPAddrUtil {

    private static final String IPV6_SEPERATOR = ":";
    private static String LOCAL_IP;
    private static String LOCAL_HOST;
    private static int IP_2_INT = 0;

    public static String localAddress() {
        if (LOCAL_IP == null) {
            try {
                for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                    NetworkInterface networkInterface = interfaces.nextElement();
                    if (networkInterface.isLoopback() || networkInterface.isVirtual() || networkInterface.isPointToPoint()
                            || !networkInterface.isUp()) {
                        continue;
                    }
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    InetAddress inet;
                    while (addresses.hasMoreElements()) {
                        inet = addresses.nextElement();
                        if (!inet.isLinkLocalAddress() && !inet.isLoopbackAddress() && !inet.isMulticastAddress() && !inet.isAnyLocalAddress()
                                && inet.isSiteLocalAddress() && !inet.getHostAddress().contains(IPV6_SEPERATOR)) {
                            LOCAL_IP = inet.getHostAddress();
                            break;
                        }
                    }
                }
            } catch (SocketException e) {
                LOCAL_IP = "";
            }
        }
        return LOCAL_IP;
    }

    public static int ip2int(String ip) {
        if (IP_2_INT == 0) {
            String localIp = localAddress();
            if (localIp == null || localIp.trim().length() == 0) {
                IP_2_INT = -1;
            } else {
                try {
                    final String[] addressBytes = ip.split("\\.");
                    int n = 0;
                    for (int i = 0; i < 4; i++) {
                        n <<= 8;
                        n |= Integer.parseInt(addressBytes[i]);
                    }
                    IP_2_INT = n;
                } catch (Throwable t) {
                    IP_2_INT = -1;
                }
            }
        }
        return IP_2_INT;
    }

    public static String getHostname() {
        if (LOCAL_HOST == null) {
            try {
                InetAddress InetAddr = InetAddress.getLocalHost();
                if (InetAddr != null) {
                    LOCAL_HOST = InetAddr.getHostName();
                }
            } catch (UnknownHostException e) {
                LOCAL_HOST = "";
            }
        }
        return LOCAL_HOST;
    }

    public static void main(String[] args) {
        System.out.println(IPAddrUtil.localAddress());
    }

}
