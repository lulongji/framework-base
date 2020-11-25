package com.lulj.base.utils.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * This class contains function for hashing.
 * <p>
 * The original code for the hash function at <a
 * href="http://burtleburtle.net/bob/hash/evahash.html">Bob</a>.
 */
public final class HashUtil {
    private HashUtil() {
    }

    static final int SEED32 = 0x12b9b0a1;

    static final long SEED64 = 0x2b992ddfa23249d6L;

    static final int CONSTANT32 = 0x9e3779b9;

    static final long CONSTANT64 = 0xe08c1d668b756f82L;

    /**
     * Hash a string to a 32 bit value.
     */
    public static int hash32(String value) {
        return hash32(value, SEED32);
    }

    /**
     * Hash a string to a 32 bit value using the supplied seed.
     */
    public static int hash32(String value, int seed) {
        if (value == null) {
            return hash32(null, 0, 0, seed);
        }
        return hash32(value.getBytes(), seed);
    }

    /**
     * Hash a string to a 64 bit value.
     */
    public static long hash64(String value) {
        return hash64(value, SEED64);
    }

    /**
     * Hash a string to a 64 bit value using the supplied seed.
     */
    public static long hash64(String value, long seed) {
        if (value == null) {
            return hash64(null, 0, 0, seed);
        }
        return hash64(value.getBytes(), seed);
    }

    /**
     * Generates a fingerprint of an input string.
     */
    public static long fingerprint(String value) {
        try {
            byte[] temp = value.getBytes("UTF-8");
            return fingerprint(temp, 0, temp.length);
        } catch (java.io.UnsupportedEncodingException e) {
            return 0;
        }
    }

    /**
     * Hash byte array to a 32 bit value.
     */
    public static int hash32(byte[] value) {
        return hash32(value, 0, value == null ? 0 : value.length);
    }

    /**
     * Hash byte array to a 32 bit value.
     */
    public static int hash32(byte[] value, int offset, int length) {
        return hash32(value, offset, length, SEED32);
    }

    /**
     * Hash byte array to a 32 bit value using the supplied seed.
     */
    public static int hash32(byte[] value, int seed) {
        return hash32(value, 0, value == null ? 0 : value.length, seed);
    }

    /**
     * Hash byte array to a 32 bit value using the supplied seed.
     */
    public static int hash32(byte[] value, int offset, int length, int seed) {
        int a = CONSTANT32;
        int b = a;
        int c = seed;
        int keylen;

        for (keylen = length; keylen >= 12; keylen -= 12, offset += 12) {
            a += word32At(value, offset);
            b += word32At(value, offset + 4);
            c += word32At(value, offset + 8);

            // Mix
            a -= b;
            a -= c;
            a ^= c >>> 13;
            b -= c;
            b -= a;
            b ^= a << 8;
            c -= a;
            c -= b;
            c ^= b >>> 13;
            a -= b;
            a -= c;
            a ^= c >>> 12;
            b -= c;
            b -= a;
            b ^= a << 16;
            c -= a;
            c -= b;
            c ^= b >>> 5;
            a -= b;
            a -= c;
            a ^= c >>> 3;
            b -= c;
            b -= a;
            b ^= a << 10;
            c -= a;
            c -= b;
            c ^= b >>> 15;
        }

        c += length;
        switch (keylen) {
            case 11:
                c += (value[offset + 10]) << 24;
            case 10:
                c += (value[offset + 9] & 0xff) << 16;
            case 9:
                c += (value[offset + 8] & 0xff) << 8;
            case 8:
                b += word32At(value, offset + 4);
                a += word32At(value, offset);
                break;
            case 7:
                b += (value[offset + 6] & 0xff) << 16;
            case 6:
                b += (value[offset + 5] & 0xff) << 8;
            case 5:
                b += (value[offset + 4] & 0xff);
            case 4:
                a += word32At(value, offset);
                break;
            case 3:
                a += (value[offset + 2] & 0xff) << 16;
            case 2:
                a += (value[offset + 1] & 0xff) << 8;
            case 1:
                a += (value[offset + 0] & 0xff);
                // case 0 : nothing left to add
        }
        return mix32(a, b, c);
    }

    /**
     * Hash byte array to a 64 bit value.
     */
    public static long hash64(byte[] value) {
        return hash64(value, 0, value == null ? 0 : value.length);
    }

    /**
     * Hash byte array to a 64 bit value.
     */
    public static long hash64(byte[] value, int offset, int length) {
        return hash64(value, offset, length, SEED64);
    }

    /**
     * Hash byte array to a 64 bit value using the supplied seed.
     */
    public static long hash64(byte[] value, long seed) {
        return hash64(value, 0, value == null ? 0 : value.length, seed);
    }

    /**
     * Hash byte array to a 64 bit value using the supplied seed.
     */
    public static long hash64(byte[] value, int offset, int length, long seed) {
        long a = CONSTANT64;
        long b = a;
        long c = seed;
        int keylen;

        for (keylen = length; keylen >= 24; keylen -= 24, offset += 24) {
            a += word64At(value, offset);
            b += word64At(value, offset + 8);
            c += word64At(value, offset + 16);

            // Mix
            a -= b;
            a -= c;
            a ^= c >>> 43;
            b -= c;
            b -= a;
            b ^= a << 9;
            c -= a;
            c -= b;
            c ^= b >>> 8;
            a -= b;
            a -= c;
            a ^= c >>> 38;
            b -= c;
            b -= a;
            b ^= a << 23;
            c -= a;
            c -= b;
            c ^= b >>> 5;
            a -= b;
            a -= c;
            a ^= c >>> 35;
            b -= c;
            b -= a;
            b ^= a << 49;
            c -= a;
            c -= b;
            c ^= b >>> 11;
            a -= b;
            a -= c;
            a ^= c >>> 12;
            b -= c;
            b -= a;
            b ^= a << 18;
            c -= a;
            c -= b;
            c ^= b >>> 22;
        }

        c += length;
        switch (keylen) {
            case 23:
                c += ((long) value[offset + 22]) << 56;
            case 22:
                c += (value[offset + 21] & 0xffL) << 48;
            case 21:
                c += (value[offset + 20] & 0xffL) << 40;
            case 20:
                c += (value[offset + 19] & 0xffL) << 32;
            case 19:
                c += (value[offset + 18] & 0xffL) << 24;
            case 18:
                c += (value[offset + 17] & 0xffL) << 16;
            case 17:
                c += (value[offset + 16] & 0xffL) << 8;
            case 16:
                b += word64At(value, offset + 8);
                a += word64At(value, offset);
                break;
            case 15:
                b += (value[offset + 14] & 0xffL) << 48;
            case 14:
                b += (value[offset + 13] & 0xffL) << 40;
            case 13:
                b += (value[offset + 12] & 0xffL) << 32;
            case 12:
                b += (value[offset + 11] & 0xffL) << 24;
            case 11:
                b += (value[offset + 10] & 0xffL) << 16;
            case 10:
                b += (value[offset + 9] & 0xffL) << 8;
            case 9:
                b += (value[offset + 8] & 0xffL);
            case 8:
                a += word64At(value, offset);
                break;
            case 7:
                a += (value[offset + 6] & 0xffL) << 48;
            case 6:
                a += (value[offset + 5] & 0xffL) << 40;
            case 5:
                a += (value[offset + 4] & 0xffL) << 32;
            case 4:
                a += (value[offset + 3] & 0xffL) << 24;
            case 3:
                a += (value[offset + 2] & 0xffL) << 16;
            case 2:
                a += (value[offset + 1] & 0xffL) << 8;
            case 1:
                a += (value[offset + 0] & 0xffL);
        }
        return mix64(a, b, c);
    }

    public static int word32At(byte[] bytes, int offset) {
        return bytes[offset + 0] + (bytes[offset + 1] << 8)
                + (bytes[offset + 2] << 16) + (bytes[offset + 3] << 24);
    }

    private static long word64At(byte[] bytes, int offset) {
        return (bytes[offset + 0] & 0xffL) + ((bytes[offset + 1] & 0xffL) << 8)
                + ((bytes[offset + 2] & 0xffL) << 16)
                + ((bytes[offset + 3] & 0xffL) << 24)
                + ((bytes[offset + 4] & 0xffL) << 32)
                + ((bytes[offset + 5] & 0xffL) << 40)
                + ((bytes[offset + 6] & 0xffL) << 48)
                + ((bytes[offset + 7] & 0xffL) << 56);
    }

    /**
     * Generates a fingerprint of the specified bytes.
     */
    public static long fingerprint(byte[] value) {
        return fingerprint(value, 0, value == null ? 0 : value.length);
    }

    /**
     * Generates a fingerprint of bytes
     */
    public static long fingerprint(byte[] value, int offset, int length) {
        int hi = hash32(value, offset, length, 0);
        int lo = hash32(value, offset, length, 102072);
        if ((hi == 0) && (lo == 0 || lo == 1)) {
            hi ^= 0x130f9bef;
            lo ^= 0x94a0a928;
        }
        return (((long) hi) << 32) | (lo & 0xffffffffl);
    }

    public static int hash32(ByteBuffer buf, int length) {
        if (buf.order() != ByteOrder.LITTLE_ENDIAN)
            throw new IllegalArgumentException("must be little endian");

        int a = CONSTANT32;
        int b = a;
        int c = SEED32;

        int numGroups = length / 12;
        for (int i = 0; i < numGroups; i++) {
            a += getInt(buf);
            b += getInt(buf);
            c += getInt(buf);

            // Mix
            a -= b;
            a -= c;
            a ^= c >>> 13;
            b -= c;
            b -= a;
            b ^= a << 8;
            c -= a;
            c -= b;
            c ^= b >>> 13;
            a -= b;
            a -= c;
            a ^= c >>> 12;
            b -= c;
            b -= a;
            b ^= a << 16;
            c -= a;
            c -= b;
            c ^= b >>> 5;
            a -= b;
            a -= c;
            a ^= c >>> 3;
            b -= c;
            b -= a;
            b ^= a << 10;
            c -= a;
            c -= b;
            c ^= b >>> 15;
        }

        c += length;
        int position = buf.position();
        switch (length - numGroups * 12) {
            case 11:
                c += (buf.get(position + 10)) << 24;
            case 10:
                c += (buf.get(position + 9) & 0xff) << 16;
            case 9:
                c += (buf.get(position + 8) & 0xff) << 8;
            case 8:
                b += getInt(buf, position + 4);
                a += getInt(buf, position);
                break;
            case 7:
                b += (buf.get(position + 6) & 0xff) << 16;
            case 6:
                b += (buf.get(position + 5) & 0xff) << 8;
            case 5:
                b += (buf.get(position + 4) & 0xff);
            case 4:
                a += getInt(buf, position);
                break;
            case 3:
                a += (buf.get(position + 2) & 0xff) << 16;
            case 2:
                a += (buf.get(position + 1) & 0xff) << 8;
            case 1:
                a += (buf.get(position) & 0xff);
        }

        return mix32(a, b, c);
    }

    /**
     * Returns the hash64 of the specified number of bytes in the specified byte
     * buffer, starting at its current position.
     */
    public static long hash64(ByteBuffer buf, int length) {
        if (buf.order() != ByteOrder.LITTLE_ENDIAN)
            throw new IllegalArgumentException("must be little endian");

        long a = CONSTANT64;
        long b = a;
        long c = SEED64;

        int numGroups = length / 24;
        for (int i = 0; i < numGroups; i++) {
            a += buf.getLong();
            b += buf.getLong();
            c += buf.getLong();

            // Mix
            a -= b;
            a -= c;
            a ^= c >>> 43;
            b -= c;
            b -= a;
            b ^= a << 9;
            c -= a;
            c -= b;
            c ^= b >>> 8;
            a -= b;
            a -= c;
            a ^= c >>> 38;
            b -= c;
            b -= a;
            b ^= a << 23;
            c -= a;
            c -= b;
            c ^= b >>> 5;
            a -= b;
            a -= c;
            a ^= c >>> 35;
            b -= c;
            b -= a;
            b ^= a << 49;
            c -= a;
            c -= b;
            c ^= b >>> 11;
            a -= b;
            a -= c;
            a ^= c >>> 12;
            b -= c;
            b -= a;
            b ^= a << 18;
            c -= a;
            c -= b;
            c ^= b >>> 22;
        }

        c += length;
        int position = buf.position();
        switch (length - numGroups * 24) {
            case 23:
                c += ((long) buf.get(position + 22)) << 56;
            case 22:
                c += (buf.get(position + 21) & 0xffL) << 48;
            case 21:
                c += (buf.get(position + 20) & 0xffL) << 40;
            case 20:
                c += (buf.get(position + 19) & 0xffL) << 32;
            case 19:
                c += (buf.get(position + 18) & 0xffL) << 24;
            case 18:
                c += (buf.get(position + 17) & 0xffL) << 16;
            case 17:
                c += (buf.get(position + 16) & 0xffL) << 8;
            case 16:
                b += buf.getLong(position + 8);
                a += buf.getLong(position);
                break;
            case 15:
                b += (buf.get(position + 14) & 0xffL) << 48;
            case 14:
                b += (buf.get(position + 13) & 0xffL) << 40;
            case 13:
                b += (buf.get(position + 12) & 0xffL) << 32;
            case 12:
                b += (buf.get(position + 11) & 0xffL) << 24;
            case 11:
                b += (buf.get(position + 10) & 0xffL) << 16;
            case 10:
                b += (buf.get(position + 9) & 0xffL) << 8;
            case 9:
                b += (buf.get(position + 8) & 0xffL);
            case 8:
                a += buf.getLong(position);
                break;
            case 7:
                a += (buf.get(position + 6) & 0xffL) << 48;
            case 6:
                a += (buf.get(position + 5) & 0xffL) << 40;
            case 5:
                a += (buf.get(position + 4) & 0xffL) << 32;
            case 4:
                a += (buf.get(position + 3) & 0xffL) << 24;
            case 3:
                a += (buf.get(position + 2) & 0xffL) << 16;
            case 2:
                a += (buf.get(position + 1) & 0xffL) << 8;
            case 1:
                a += (buf.get(position + 0) & 0xffL);
        }

        return mix64(a, b, c);
    }

    private static int getInt(ByteBuffer buf) {
        return addSignCruft(buf.getInt());
    }

    private static int getInt(ByteBuffer buf, int pos) {
        return addSignCruft(buf.getInt(pos));
    }

    private static int addSignCruft(int i) {
        return (i & ~0x80808080) - (i & 0x80808080);
    }

    static int mix32(int a, int b, int c) {
        a -= b;
        a -= c;
        a ^= c >>> 13;
        b -= c;
        b -= a;
        b ^= a << 8;
        c -= a;
        c -= b;
        c ^= b >>> 13;
        a -= b;
        a -= c;
        a ^= c >>> 12;
        b -= c;
        b -= a;
        b ^= a << 16;
        c -= a;
        c -= b;
        c ^= b >>> 5;
        a -= b;
        a -= c;
        a ^= c >>> 3;
        b -= c;
        b -= a;
        b ^= a << 10;
        c -= a;
        c -= b;
        c ^= b >>> 15;
        return c;
    }

    static long mix64(long a, long b, long c) {
        a -= b;
        a -= c;
        a ^= c >>> 43;
        b -= c;
        b -= a;
        b ^= a << 9;
        c -= a;
        c -= b;
        c ^= b >>> 8;
        a -= b;
        a -= c;
        a ^= c >>> 38;
        b -= c;
        b -= a;
        b ^= a << 23;
        c -= a;
        c -= b;
        c ^= b >>> 5;
        a -= b;
        a -= c;
        a ^= c >>> 35;
        b -= c;
        b -= a;
        b ^= a << 49;
        c -= a;
        c -= b;
        c ^= b >>> 11;
        a -= b;
        a -= c;
        a ^= c >>> 12;
        b -= c;
        b -= a;
        b ^= a << 18;
        c -= a;
        c -= b;
        c ^= b >>> 22;
        return c;
    }

//    /**
//     * HmacSHA256加密
//     *
//     * @param key  密钥
//     * @param data 数据
//     * @return 加密结果
//     */
//    public static String encode(String key, String data) {
//        String code;
//        try {
//            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
//            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
//            sha256_HMAC.init(secret_key);
//            code = Hex.toHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
//        } catch (Exception e) {
//            throw new RuntimeException("密码编码错误");
//        }
//        return code;
//    }
}
