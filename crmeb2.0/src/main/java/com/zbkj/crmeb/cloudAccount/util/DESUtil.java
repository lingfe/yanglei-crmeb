package com.zbkj.crmeb.cloudAccount.util;

import com.zbkj.crmeb.cloudAccount.constant.ConfigPath;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DESUtil {

    // 3DES加密
    public static String encode(String data) throws Exception {
        byte[] content = data.getBytes("utf-8");
        byte[] key = Property.getProperties(ConfigPath.YZH_3DESKEY).getBytes("utf-8");

        byte[] enc = TripleDesEncrypt(content, key);
        byte[] enc64 = Base64.encodeBase64(enc);
        return new String(enc64);
    }

    /**
        解密报错 javax.crypto.IllegalBlockSizeException: Input length must be multiple of 8 when decrypting with padded cipher
        不需要进行urldecode，使用如下解密代码
        byte[] dec64 = Base64.decodeBase64(notifyResponse.getData());
    **/
    // 3DES解密
    public static String decode(String data) throws Exception {
        byte[] dec64 = Base64.decodeBase64(data);
        byte[] dec = TripleDesDecrypt(dec64, Property.getProperties(ConfigPath.YZH_3DESKEY).getBytes("utf-8"));
        return new String(dec);
    }
    public static String decode(String data,String deskey) throws Exception {
        byte[] dec64 = Base64.decodeBase64(data);
        byte[] dec = TripleDesDecrypt(dec64, deskey.getBytes("utf-8"));
        return new String(dec);
    }

    public static byte[] TripleDesEncrypt(byte[] content, byte[] key) throws Exception {
        byte[] icv = new byte[8];
        System.arraycopy(key, 0, icv, 0, 8);
        return TripleDesEncrypt(content, key, icv);
    }

    protected static byte[] TripleDesEncrypt(byte[] content, byte[] key, byte[] icv) throws Exception {
        final SecretKey secretKey = new SecretKeySpec(key, "DESede");
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        final IvParameterSpec iv = new IvParameterSpec(icv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        return cipher.doFinal(content);
    }

    public static byte[] TripleDesDecrypt(byte[] content, byte[] key) throws Exception {
        byte[] icv = new byte[8];
        System.arraycopy(key, 0, icv, 0, 8);
        return TripleDesDecrypt(content, key, icv);
    }

    protected static byte[] TripleDesDecrypt(byte[] content, byte[] key, byte[] icv) throws Exception {
        final SecretKey secretKey = new SecretKeySpec(key, "DESede");
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        final IvParameterSpec iv = new IvParameterSpec(icv);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        return cipher.doFinal(content);
    }

}
