package com.supereal.bigfile.controller;

import com.supereal.bigfile.form.FileForm;
import com.supereal.bigfile.service.UploadFileService;
import com.supereal.bigfile.utils.Base64Util;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * Create by tianci
 * 2019/1/10 15:41
 */
@Controller
@RequestMapping("/file")
public class UploadFileController {

    @Autowired
    UploadFileService uploadFileService;

    @GetMapping("/index")
    public String open() {

        return "upload";
    }

    @PostMapping("/isUpload")
    @ResponseBody
    public Map<String, Object> isUpload(@Valid FileForm form) {

        return uploadFileService.findByFileMd5(form.getMd5());

    }
    public static String AESDecrypt(byte[] encryptBytes,String key)throws Exception
    {
        KeyGenerator kgen=KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher=Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE,new SecretKeySpec(key.getBytes(),"AES"));
        byte[] decryptBytes=cipher.doFinal(encryptBytes);
        return new String(decryptBytes,"utf8");
}


    @PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> upload(@Valid FileForm form,
                                      @RequestParam(value = "fileEncrypt", required = false)String fileEncrypt,@RequestParam(value = "key", required = false)String key,
                                      @RequestParam(value = "temp", required = false)MultipartFile file1)throws Exception {
      /*  Map<String, Object> map = null;
        Base64 base64 = new Base64();
        try {
                if (form.getAction().equals("check"))
                {
                    map=uploadFileService.check(form);
                }
                if (form.getAction().equals("upload")) {
                    String temp=new String(base64.decode(fileEncrypt), "UTF-8");
                 String   decrypt = AESDecrypt(temp.getBytes("utf8"), key);
                 System.out.println(decrypt);
                InputStream inputStream = new ByteArrayInputStream(decrypt.getBytes("utf8"));
                MultipartFile file = new MockMultipartFile(form.getName(), inputStream);
                map = uploadFileService.realUpload(form, file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;*/
        Map<String, Object> map = null;
        Base64 base64=new Base64();
        try {
            if (form.getAction().equals("check"))
            {
                map=uploadFileService.check(form);
            }
            if (form.getAction().equals("upload")) {
                System.out.println(key);
                String decrypt = AESDecrypt(base64.decode(fileEncrypt), key);
                InputStream inputStream = new ByteArrayInputStream(decrypt.getBytes());
                MultipartFile file = new MockMultipartFile(form.getName(), inputStream);
                map = uploadFileService.realUpload(form, file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
