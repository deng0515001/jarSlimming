package com.dengxq.jarslimming.utils;

public class HexUtil {

    /**
     * 十六进制转换字符串
     *
     * @param hexStr Byte字符串
     * @return String 对应的字符串
     */
    public static String hexStr2Str(String hexStr) {
        if (hexStr == null) {
            return "";
        }
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * bytes转换成十六进制字符串
     *
     * @param bytes byte数组
     * @return 每个Byte值之间空格分隔
     */
    public static String byte2HexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder("");
        for (byte b : bytes) {
            String tmp = Integer.toHexString(b & 0xFF);
            sb.append((tmp.length() == 1) ? "0" + tmp : tmp);
        }
        return sb.toString().toUpperCase().trim();
    }
}