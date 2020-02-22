package com.supereal.bigfile.repository;

import com.supereal.bigfile.dataobject.UploadFile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;


/**
 * Create by tianci
 * 2019/1/10 15:02
 */
@Component
@Mapper
public interface UploadFileRepository {

    UploadFile findFileByFileMd5(String fileMd5);

    void saveFile(UploadFile file);

}
