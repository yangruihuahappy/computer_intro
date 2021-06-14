package com.introtoc.videoService.service;

import com.aliyuncs.exceptions.ClientException;
import org.springframework.web.multipart.MultipartFile;

public interface VideoService {
    //上传视频到阿里云
    String uploadVideoAly(MultipartFile file);

    //云端删除视频
    void removeVideo(String videoId);
    //根据视频id获取视频凭证
    String getPlayAuthById(String videoId) throws ClientException;
}
