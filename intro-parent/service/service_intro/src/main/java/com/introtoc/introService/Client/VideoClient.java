package com.introtoc.introService.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

@FeignClient("service-video")
@Component
public interface VideoClient {

    //定义调用的方法路径
    //云端添加视频  //暂时不用
    @PostMapping("/videoService/video/uploadAlyVideo")
    public String uploadAlyVideo(MultipartFile file);
}
