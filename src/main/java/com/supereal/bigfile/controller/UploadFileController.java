package com.supereal.bigfile.controller;

import com.supereal.bigfile.form.FileForm;
import com.supereal.bigfile.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.Convert;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
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
        return new String(decryptBytes);
    }


    @PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> upload(@Valid FileForm form,
                                      @RequestParam(value = "fileEncrypt", required = false)String fileEncrypt,@RequestParam(value = "key", required = false)String key)throws Exception {
        Map<String, Object> map = null;

        try {
                if (form.getAction().equals("check"))
                {
                    map=uploadFileService.check(form);
                }
                if (form.getAction().equals("upload")) {
                System.out.println(key);
                String decrypt = AESDecrypt(Base64.getDecoder().decode(fileEncrypt), key);
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
