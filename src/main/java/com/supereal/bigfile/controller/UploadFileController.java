package com.supereal.bigfile.controller;

import com.mysql.cj.util.Base64Decoder;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.supereal.bigfile.form.FileForm;
import com.supereal.bigfile.service.UploadFileService;
import com.supereal.bigfile.utils.AESUtil;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
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
                System.out.println("key = " + key);
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
