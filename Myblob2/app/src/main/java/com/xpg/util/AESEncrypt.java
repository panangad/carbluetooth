package com.xpg.util;

import com.xpg.constant.Constants;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncrypt {
    private static String AES_HEX = "112b1ea14ae0ac4c081c26b4974b03f8c41d40cea3418eba6c0203404cb470bf";
    private Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    private IvParameterSpec dps;
    private SecretKeySpec skeySpec;

    public AESEncrypt() throws Exception {
        byte[] passkey = hex2Bin(AES_HEX);
        byte[] key = getAESKey(passkey);
        this.dps = new IvParameterSpec(getAESIV(passkey));
        this.skeySpec = new SecretKeySpec(key, "AES");
    }

    private byte[] getAESIV(byte[] keyRaw) throws Exception {
        byte[] iv = new byte[16];
        System.arraycopy(keyRaw, 8, iv, 0, 16);
        return iv;
    }

    private byte[] getAESKey(byte[] keyRaw) throws Exception {
        byte[] key = new byte[16];
        System.arraycopy(keyRaw, 0, key, 0, 8);
        System.arraycopy(keyRaw, 24, key, 8, 8);
        return key;
    }

    public String encrypt(String command) throws Exception {
        this.cipher.init(1, this.skeySpec, this.dps);
        return byte2hexString(this.cipher.doFinal(command.getBytes()));
    }

    public String decrypt(String sSrc) throws Exception {
        this.cipher.init(2, this.skeySpec, this.dps);
        return new String(this.cipher.doFinal(hex2Bin(sSrc)));
    }

    private byte[] hex2Bin(String src) {
        if (src.length() < 1) {
            return null;
        }
        byte[] encrypted = new byte[(src.length() / 2)];
        for (int i = 0; i < src.length() / 2; i++) {
            int high = Integer.parseInt(src.substring(i * 2, (i * 2) + 1), 16);
            encrypted[i] = (byte) ((high * 16) + Integer.parseInt(src.substring((i * 2) + 1, (i * 2) + 2), 16));
        }
        return encrypted;
    }

    private String byte2hexString(byte[] buf) {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        for (int i = 0; i < buf.length; i++) {
            if ((buf[i] & 255) < 16) {
                strbuf.append("0");
            }
            strbuf.append(Long.toString((long) (buf[i] & 255), 16));
        }
        return strbuf.toString();
    }

    public boolean validate(String auothCode, String machineCode) {
        try {
            String s = decrypt(auothCode);
            if (!(s == null || s.equals(Constants.CURRENT_MODEL) || !machineCode.equals(s))) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        AESEncrypt aESEncrypt;
        Exception e;
        try {
            AESEncrypt aes = new AESEncrypt();
            try {
                System.out.println(aes.encrypt("32767236786237867823"));
                aESEncrypt = aes;
            } catch (Exception e2) {
                e = e2;
                aESEncrypt = aes;
                e.printStackTrace();
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
        }
    }
}
