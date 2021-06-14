package com.introtoc.ossService.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {

    //上传文件到oss
    String uploadFileExperiment(MultipartFile file);

    //下载文件
    void loadFile(String url);

    //云端删除文件
    void removeFile(String fileId);
}
