package com.lulj.base.utils.http;

import java.io.*;

/**
 * @author lu
 */
public class StreamUtil {
    @SuppressWarnings("unused")
    public static String readContentByStream(InputStream is) {
        BufferedReader reader = null;
        String line = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                sb.append(line.trim()).append("\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString().trim();
    }

    public static String readContentByReader(String path) {
        char[] buf = new char[1024];
        StringBuilder out = new StringBuilder();
        try {
            Reader in = new BufferedReader(new FileReader(new File(path)));
            int bin;
            while ((bin = in.read(buf, 0, buf.length)) >= 0) {
                out.append(buf, 0, bin);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    @SuppressWarnings("unused")
    public static String readContentByFile(String path) {
        BufferedReader reader = null;
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(path);
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                sb.append(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString().trim();
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString().trim();
    }
}