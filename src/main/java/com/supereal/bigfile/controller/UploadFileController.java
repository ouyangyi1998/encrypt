package com.supereal.bigfile.controller;

import com.supereal.bigfile.form.FileForm;
import com.supereal.bigfile.service.UploadFileService;
import com.supereal.bigfile.utils.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by tianci
 * 2019/1/10 15:41
 */
@Controller
@RequestMapping("/file")
@Slf4j
public class UploadFileController {

    @Autowired
    UploadFileService uploadFileService;

    /**
     * 密钥长度 于原文长度对应 以及越长速度越慢
     */
    private final static int KEY_SIZE = 1024;
    /**
     * 用于封装随机产生的公钥与私钥
     */
    private static Map<Integer, String> keyMap = new HashMap<Integer, String>();

    /**
     * 随机生成密钥对
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器
        keyPairGen.initialize(KEY_SIZE, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        // 得到私钥字符串
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        // 将公钥和私钥保存到Map
        //0表示公钥
        keyMap.put(0, publicKeyString);
        //1表示私钥
        keyMap.put(1, privateKeyString);
    }

    public static String decrypt(String str, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.getDecoder().decode(str);
        //base64编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }




    @GetMapping("/index")
    public String open() {

        return "upload";
    }

    @PostMapping("/isUpload")
    @ResponseBody
    public Map<String, Object> isUpload(@Valid FileForm form)throws Exception {
        Map<String,Object> map=new HashMap<String, Object>();
        map= uploadFileService.findByFileMd5(form.getMd5());
        if ((int)map.get("flag")!=2)
        {
            genKeyPair();
            System.out.println("PublicKey为"+keyMap.get(0));
            System.out.println("PrivateKey为"+keyMap.get(1));
            map.put("RsaPublicKey",keyMap.get(0));
        }
        return map;
    }


    public static byte[] hexToByte(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> upload(@Valid FileForm form,
                                      @RequestParam(value = "fileEncrypt", required = false)String fileEncrypt,
                                      @RequestParam(value = "key", required = false)String key)throws Exception {
        Map<String, Object> map = null;

        try {
            if (form.getAction().equals("check"))
            {
                map=uploadFileService.check(form);
            }
            if (form.getAction().equals("upload")) {
                System.out.println("被rsa加密的key = " + key);
                //先进行rsa解密
                key = decrypt(key,keyMap.get(1));
                System.out.println("rsa解密之后的key"+key);
                //再进行aes解密
                String decrypt = AESUtil.decrypt(fileEncrypt,key);
                InputStream inputStream = new ByteArrayInputStream(hexToByte(decrypt));
                MultipartFile file = new MockMultipartFile(form.getName(), inputStream);
                map = uploadFileService.realUpload(form, file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
