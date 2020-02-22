package com.supereal.bigfile.dataobject;

import lombok.Data;

import java.util.Date;

/**
 * Create by tianci
 * 2019/1/10 14:38
 */

@Data
public class UploadFile {

    /* uuid */
    private String fileId;

    /* 文件路径 */
    private String filePath;

    /* 文件大小 */
    private String fileSize;

    /* 文件后缀 */
    private String fileSuffix;

    /* 文件名字 */
    private String fileName;

    /* 文件md5 */
    private String fileMd5;

    /* 文件上传状态 */
    private Integer fileStatus;

    private Date createTime;

    private Date updateTime;
}
