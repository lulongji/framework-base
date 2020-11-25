package com.lulj.base.utils.cryptos;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

/**
 * 报文加密解密
 *
 * @author lvzhen
 */

public class Cryptos {

    public final static String SECRET_KEY = "06dc87af5f37a004da50ceeb32a1b9c7";

    public final static String SECRET_CONF_KEY = "10C173A98BAD2FF723BE0E81A9D85965";

    public Cryptos() {

    }

    /*快速加密算法码表，请勿修改*/
    private static final int[] rev = {0x0b, 0x04, 0x0f, 0x06, 0x01, 0x0a, 0x03, 0x09, 0x0d, 0x07, 0x05, 0x00, 0x0e,
            0x08, 0x0c, 0x02};
    private static final int[] sk = {0xd7, 0x6a, 0xa4, 0x78, 0xf5, 0x7c, 0x42, 0xab, 0xa4, 0x52, 0xf6, 0x76, 0x3b,
            0x4d, 0x61, 0xce};

    //--业务方法--//

    //--快速解密算法，输入key和byte[]，缺省长度为byte[]长度--//
    public static String crypto_quick_dec_ByteArr_Tx(vm_crypto_quick_key_t key, byte[] src) {
        int[] dst = crypto_quick_dec_ByteArr(key, src, src.length);
        byte[] tb = new byte[dst.length];
        for (int i = 0; i < dst.length; i++) {
            tb[i] = (byte) dst[i];
        }
        return new String(tb);
    }

    //--快速解密算法，输入key和byte[]，缺省长度为byte[]长度--//
    public static byte[] crypto_quick_dec_ByteArr_Tx(vm_crypto_quick_key_t key, byte[] src, int len) {
        int[] dst = crypto_quick_dec_ByteArr(key, src, len);
        byte[] tb = new byte[dst.length];
        for (int i = 0; i < dst.length; i++) {
            tb[i] = (byte) dst[i];
        }
        return tb;
    }

    //--快速解密算法，输入key和byte[]及被加密字串长度--//
    public static int[] crypto_quick_dec_ByteArr(vm_crypto_quick_key_t key, byte[] bt, int len) {
        //--处理输入内容，byte[]转为int[]--//
        int[] dst = new int[bt.length];
        for (int i = 0; i < bt.length; i++) {
            try {
                dst[i] = Byte2Int(bt[i]);//byte转int
            } catch (Exception e) {
                dst[i] = 0;////可能出问题
                e.printStackTrace();
            }
        }
        //解密
        return crypto_quick_dec(key, dst, len);
    }

    /************************************************************************
     *	函数名：crypto_quick_dec
     *	功	    能：快速解密算法，输出为8倍整数
     *			解密处理流程：8字节数据 = > 与码表异或 = > 与key异或  = > 查表替换 = >
     *			每字节4bit反序  = > 8字节反序处理  = > 生成目标数据  ( == > 向量数据处理 )
     *	返回值：int:	解密长度
     *			-1:	失败
     *	作	    者：
     ************************************************************************/

    public static int[] crypto_quick_dec(vm_crypto_quick_key_t key, int[] src, int len) {
        int lenf = len;
        int[] dst = new int[len + 8];
        int[] r = new int[8];
        int[] rk = new int[8];
        int[] iv = new int[8];
        System.arraycopy(key.getRk(), 0, rk, 0, key.getRk().length);//copy arry
        System.arraycopy(key.getIv(), 0, iv, 0, key.getIv().length);//copy arry
        int src_f = 0;
        int dst_f = 0;

        while (lenf > 0) {
            for (int n = 0; n < 8; n++) {
                if (src.length > (src_f + n)) {
                    r[n] = src[src_f + n] ^ rk[n] ^ sk[n];
                    dst[dst_f + 7 - n] = (rev[r[n] >> 4] + (rev[r[n] & 0xF] << 4)) ^ iv[n];
                    iv[n] = src[src_f + n];
                } else {
                    r[n] = 0 ^ rk[n] ^ sk[n];
                    dst[dst_f + 7 - n] = (rev[r[n] >> 4] + (rev[r[n] & 0xF] << 4)) ^ iv[n];
                    iv[n] = 0;
                }
            }
            lenf -= 8;
            src_f += 8;
            dst_f += 8;
        }
        ///////////////////
        int[] dst_t = new int[len];
        System.arraycopy(dst, 0, dst_t, 0, len);//copy arry
        return dst_t;
    }

    //--快速加密算法，输入key和待加密的串，,输出为byte数组--//
    public static byte[] vm_crypto_quick_enc_ByteArr(vm_crypto_quick_key_t key, byte[] bt) {
        //处理输入值 ，txt->byte[]->int[]
        int[] srcArr = new int[bt.length];
        for (int i = 0; i < bt.length; i++) {
            try {
                srcArr[i] = Byte2Int(bt[i]);//byte转int
            } catch (Exception e) {
                srcArr[i] = 0;////可能出问题
                e.printStackTrace();
            }
        }
        //加密
        int[] dst1 = vm_crypto_quick_enc(key, srcArr);
        //处理返回值，int[]转byte[]
        byte[] bty = new byte[dst1.length];
        for (int i = 0; i < dst1.length; i++) {
            bty[i] = (byte) dst1[i];
        }
        //return Arrays.toString(ar);
        return bty;
    }

    /************************************************************************
     *	函数名：crypto_quick_enc
     *	功	    能：快速加密算法，输出为8倍整数,userkey按64bits展开
     *			加密处理流程：8字节数据 = > 8字节反序处理 = > 每字节4bit反序 = >
     *           查表替换 = > 与key异或 = > 与码表异或 = > 生成目标数据  ( == > 向量数据处理 )
     *	返回值：int:	加密长度
     *			      -1:	失败
     *	作	    者：
     ************************************************************************/
    public static int[] vm_crypto_quick_enc(vm_crypto_quick_key_t key, int[] src) {
        int len = src.length;
        int[] dst = new int[len + 8];
        int[] rk = new int[8];
        int[] iv = new int[8];

        System.arraycopy(key.getRk(), 0, rk, 0, key.getRk().length);//copy arry
        System.arraycopy(key.getIv(), 0, iv, 0, key.getIv().length);//copy arry
        int src_f = 0;
        int dst_f = 0;
        int flag = 0;
        //		System.out.println("\n加密_rk==");
        //		for (int s : key.getRk())
        //			System.out.print("-" + s);
        //		System.out.println("\n加密_iv==");
        //		for (int s : key.getIv())
        //			System.out.print("-" + s);
        //		System.out.println("加密_len==[" + len + "]");

        while (len > 0) {
            for (int n = 0; n < 8; n++) {
                iv[n] ^= (src.length > (src_f + 7 - n)) ? src[src_f + 7 - n] : 0;
                iv[n] = (rev[iv[n] >> 4] + (rev[iv[n] & 0xF] << 4)) ^ rk[n] ^ sk[n];
                dst[dst_f + n] = iv[n];
                flag++;
            }
            len -= 8;
            src_f += 8;
            dst_f += 8;
        }

        ///////////////////
        int[] dst_t = new int[flag];
        System.arraycopy(dst, 0, dst_t, 0, flag);//copy arry
        return dst_t;
    }

    /*初始化加解密的KEY---输入字符串*/
    public static vm_crypto_quick_key_t vm_crypto_quick_init_key_Str(String userkeyTxt) {
        char[] userkeyTxtChar = userkeyTxt.toCharArray();
        int[] userkey = new int[userkeyTxtChar.length];
        for (int i = 0; i < userkeyTxtChar.length; i++) {
            userkey[i] = (int) userkeyTxtChar[i];
        }
        return vm_crypto_quick_init_key(userkey);
    }

    //--加密all in one ，输入：key和待加密串；输出：加密后字串--//
    public static byte[] toQESEncode(String key, byte[] srcTx) {
        vm_crypto_quick_key_t keyt = vm_crypto_quick_init_key_Str(key);//初始化key
        byte[] bty = vm_crypto_quick_enc_ByteArr(keyt, srcTx);//加密
        return bty;
    }

    /***
     * 解密
     *
     * @param key
     * @param srcTx
     * @return
     */
    public static byte[] toQESDecode(String key, byte[] byteTx) {
        vm_crypto_quick_key_t keyt = vm_crypto_quick_init_key_Str(key);//初始化key
        byte[] bty = crypto_quick_dec_ByteArr_Tx(keyt, byteTx, byteTx.length);//解密
        return bty;
    }

    /*初始化加解密的KEY--输入int[]*/
    public static vm_crypto_quick_key_t vm_crypto_quick_init_key(int[] userkey) {
        int[] __smask = {0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01};//[128, 64, 32, 16, 8, 4, 2, 1]
        int[] ukey = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        int i, j;

        //按8bits展开//初始化
        vm_crypto_quick_key_t key = new vm_crypto_quick_key_t();
        key.setRk(new int[]{0, 0, 0, 0, 0, 0, 0, 0});
        key.setIv(new int[]{0, 0, 0, 0, 0, 0, 0, 0});
        for (i = 0; i < userkey.length; i++) {
            ukey[i & 0x7] ^= userkey[i];
        }
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                key.getRk()[i] |= ukey[(i + j) & 0x07] & __smask[(i + j) & 0x07];
            }
        }
        return key;
    }

    //--公用方法--//

    //--byte转int，输入单byte--//
    public static int Byte2Int(byte b) throws Exception {
        byte hh[] = new byte[4];
        hh[0] = 0;
        hh[1] = 0;
        hh[2] = 0;
        hh[3] = b;
        return ByteArrayToInt(hh);
    }

    //--byte转int，输入byte[]--//
    public static int ByteArrayToInt(byte b[]) throws Exception {
        ByteArrayInputStream buf = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(buf);
        return dis.readInt();
    }

}
