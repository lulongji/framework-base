package com.lulj.base.utils;

import com.jcraft.jzlib.DeflaterOutputStream;
import com.jcraft.jzlib.InflaterInputStream;

import java.io.*;

public class ZipUtil {

    // 输入数据的最大长度
    private static final int MAXLENGTH = 102400;
    private static final int BUFFERSIZE = 1024;

    /**
     * 压缩数据
     *
     * @param object
     * @return
     * @throws IOException
     */
    public static byte[] Compress(byte[] object) throws IOException {

        byte[] data = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DeflaterOutputStream zOut = new DeflaterOutputStream(out);
            DataOutputStream objOut = new DataOutputStream(zOut);
            objOut.write(object);
            objOut.flush();
            zOut.close();
            data = out.toByteArray();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return data;
    }

    /**
     * 解压被压缩的数据
     *
     * @param object
     * @return
     * @throws IOException
     */
    public static byte[] UnCompress(byte[] object) throws IOException {

        byte[] data = new byte[MAXLENGTH];
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(object);
            InflaterInputStream zIn = new InflaterInputStream(in);
            DataInputStream objIn = new DataInputStream(zIn);

            int len = 0;
            int count = 0;
            while ((count = objIn.read(data, len, len + BUFFERSIZE)) != -1) {
                len = len + count;
            }

            byte[] trueData = new byte[len];
            System.arraycopy(data, 0, trueData, 0, len);

            objIn.close();
            zIn.close();
            in.close();

            return trueData;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {

        // try
        // {
        // String content =
        // "发生的开发建设的房价证据法的思考积分十fjskfjsdkfjsdkfj阿飞就受到开发建设的开发建设的开发建设的疯狂解说的开发建设的开发进度上看风景的思考附件三大块非晶副教授的开发建设的开发建设的开发建设咖啡就是打开附件防辐射的看法就开始大幅降低快速反击得手辅导书开发建设的开发建设咖啡机上的非司法解释的开发建设的开发大富商的金发科技时代副教授的开发建设的福建省地方可发送到附近开始的发生大幅减少的开发建设的开发建设的";
        // System.out.println(content);
        // byte[] origin = content.getBytes();
        // System.out.println("orign length is : " + origin.length);
        // byte[] compressed = Compress(origin);
        // System.out.println("compressed length is : " + compressed.length);
        //
        // byte[] unCompressed = UnCompress(compressed);
        // System.out.println("unCompressed length is : " +
        // unCompressed.length);
        //
        // String newContent = new String(unCompressed);
        // System.out.println(newContent);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
    }

}