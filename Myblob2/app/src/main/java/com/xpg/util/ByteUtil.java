package com.xpg.util;

import com.xpg.constant.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByteUtil {
    public static String toBinaryString(int i) {
        return repairBinary(Integer.toBinaryString(i), 8);
    }

    public static String intToBinaryString(int i) {
        return Integer.toBinaryString(i);
    }

    public static String toHexString(int i) {
        return repairHex(Integer.toHexString(i).toUpperCase(), 6);
    }

    public static int toTenFromHexString(String hex) {
        return Integer.parseInt(Integer.valueOf(hex, 16).toString());
    }

    public static int toTenFromBinaryString(String binary) {
        return Integer.parseInt(Integer.valueOf(binary, 2).toString());
    }

    public static String toBinaryFromTen(int i) {
        return Integer.toBinaryString(i);
    }

    public static int leftBy(int i, int count) {
        return i << count;
    }

    public static int add(int i, int addNumber, int leftCount) {
        return (addNumber << leftCount) + i;
    }

    public static String repairBinary(String i, int expectedLength) {
        int len = i.length();
        if (len >= expectedLength) {
            return i;
        }
        int n = expectedLength - len;
        String temp = Constants.CURRENT_MODEL;
        for (int j = 0; j < n; j++) {
            temp = new StringBuilder(String.valueOf(temp)).append("0").toString();
        }
        return new StringBuilder(String.valueOf(temp)).append(i).toString();
    }

    public static String repairHex(String i, int expectedLength) {
        int len = i.length();
        if (len >= expectedLength) {
            return i;
        }
        int n = expectedLength - len;
        String temp = Constants.CURRENT_MODEL;
        for (int j = 0; j < n; j++) {
            temp = new StringBuilder(String.valueOf(temp)).append("0").toString();
        }
        return new StringBuilder(String.valueOf(temp)).append(i).toString();
    }

    public static String getResult(String result, String prefix) {
        return new StringBuilder(String.valueOf(prefix)).append(result).toString();
    }

    public static String removeZeroFromHead(String result) {
        Matcher m = Pattern.compile("[^0+]").matcher(result);
        if (m.find()) {
            return result.substring(m.start());
        }
        return result;
    }

    public static String printHexString(byte[] b) {
        String str = Constants.CURRENT_MODEL;
        for (byte b2 : b) {
            String hex = Integer.toHexString(b2 & 255);
            if (hex.length() == 1) {
                hex = new StringBuilder(String.valueOf('0')).append(hex).toString();
            }
            str = new StringBuilder(String.valueOf(str)).append(hex).toString();
        }
        return str;
    }

    public static String Bytes2HexString(byte[] b) {
        String ret = Constants.CURRENT_MODEL;
        for (byte b2 : b) {
            String hex = Integer.toHexString(b2 & 255);
            if (hex.length() == 1) {
                hex = new StringBuilder(String.valueOf('0')).append(hex).toString();
            }
            ret = new StringBuilder(String.valueOf(ret)).append(hex.toUpperCase()).toString();
        }
        return ret;
    }

    public static String Ints2HexString(int[] i) {
        String ret = Constants.CURRENT_MODEL;
        for (int j = 0; j < i.length; j++) {
            StringBuffer buf = new StringBuffer(2);
            if ((i[j] & 255) < 16) {
                buf.append("0");
            }
            buf.append(Long.toString((long) (i[j] & 255), 16));
            ret = new StringBuilder(String.valueOf(ret)).append(buf.toString()).toString();
        }
        return ret;
    }

    public static int[] bytesToInts(byte[] bytes) {
        int[] ints = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] < (byte) 0) {
                ints[i] = (-bytes[i]) + 127;
            } else {
                ints[i] = bytes[i];
            }
        }
        return ints;
    }

    public static int[] HexStringToInts(String hexString) {
        int[] ints = new int[hexString.length()];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = hexString.charAt(i);
        }
        return ints;
    }

    public static byte uniteBytes(byte src0, byte src1) {
        return (byte) (((byte) (Byte.decode("0x" + new String(new byte[]{src0})).byteValue() << 4)) ^ Byte.decode("0x" + new String(new byte[]{src1})).byteValue());
    }

    public static byte[] HexString2Bytes(String src) {
        byte[] ret = new byte[8];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < 8; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[(i * 2) + 1]);
        }
        return ret;
    }

    public static String removeHeaderX(String string) {
        if (string.charAt(0) == '0') {
            return string.substring(2, string.length());
        }
        if (string.charAt(0) == 'x') {
            return string.substring(1, string.length());
        }
        return string;
    }

    public static int[] removeHeader0X(int[] ints) {
        int[] re;
        int count;
        if (ints[0] == 48 && ints[1] == 120) {
            re = new int[(ints.length - 2)];
            count = 2;
        } else if (ints[0] == 120) {
            re = new int[(ints.length - 1)];
            count = 1;
        } else {
            re = ints;
            count = 0;
        }
        for (int i = 0; i < re.length; i++) {
            re[i] = ints[count + i];
        }
        return re;
    }

    public static byte[] copyBytes(byte[] bytes, int length) {
        byte[] copyBytes = new byte[length];
        for (int i = 0; i < length; i++) {
            copyBytes[i] = bytes[i];
        }
        return copyBytes;
    }

    public static void main(String[] args) {
        System.out.println(getResult(toHexString(4190396), "0X00"));
        System.out.println(toTenFromBinaryString("00000000001111111111000010111100"));
        System.out.println(toBinaryFromTen(11694008));
    }

    public static String showByteContent(byte[] data) {
        return long2String(bytesToLong(data));
    }

    public static long[] bytesToLong(byte[] bytes) {
        long[] ints = new long[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            ints[i] = (long) (bytes[i] & 255);
        }
        return ints;
    }

    public static String long2String(long[] data) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String hexString = Long.toHexString(data[i]);
            if (data[i] < 16) {
                sb.append("0");
            }
            sb.append(hexString);
        }
        return sb.toString();
    }
}
