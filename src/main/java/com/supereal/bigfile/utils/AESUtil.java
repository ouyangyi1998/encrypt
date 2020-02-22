
package com.supereal.bigfile.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import sun.misc.BASE64Decoder;

/**
 * 加密工具类 实现aes加密、解密
 */
@SuppressWarnings("restriction")
public class AESUtil {


    //算法PKCS5Padding
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";


    //AES解密--并解密base 64 code
    public static String decrypt(String encryptStr,String key) throws Exception {
        encryptStr.replaceAll("#", "/");
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr),key);
    }


    //base 64 encode编码
    private static String base64Encode(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    //base 64 decode解码---》因为传过来的值是通过base64编码而后再进行aes加密出来的，所以解密之前先进行base64解码
    private static byte[] base64Decode(String base64Code) throws Exception {
        return StringUtils.isEmpty(base64Code) ? null : new BASE64Decoder().decodeBuffer(base64Code);
    }

    //AES解密
    private static String aesDecryptByBytes(byte[] encryptBytes,String key) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes, "utf-8");
    }
}