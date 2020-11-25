package com.lulj.base.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author lu
 */
public class MD5Utils {

    public static String String2MD5(String str) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.reset();
        md.update(str.getBytes());
        byte[] bArray = md.digest();
        String finalStr = new String(encodeHex(bArray, hexDigits));
        return finalStr;
    }

    private static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int len = data.length;
        final char[] out = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }
}
