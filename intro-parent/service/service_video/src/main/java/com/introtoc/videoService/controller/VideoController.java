package com.introtoc.videoService.controller;

import com.aliyuncs.exceptions.ClientException;
import com.introtoc.commonUtils.R;
import com.introtoc.videoService.service.VideoService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/introVideo/video")
@CrossOrigin
public class VideoController {

    //注入服务类
    @Autowired
    private VideoService videoService;

    //上传视频到阿里云  前端上传视频时先将视频上传做处理，将得到的id封装到前端的知识点对象中
    @PostMapping("uploadAlyVideo")
    public R uploadAlyVideo(MultipartFile file){
        System.out.println("运行到了上传视频");
        String fileName = file.getOriginalFilename();
        String videoId = videoService.uploadVideoAly(file); //返回视频id
        return R.ok().data("videoId",videoId).data("fileName",fileName);
//        return videoId;
    }

    //云端删除视频
    @DeleteMapping("removeById/{videoId}")
    public R removeVideo(@ApiParam(name = "videoId", value = "云端视频id", required = true)
                         @PathVariable String videoId){

        videoService.removeVideo(videoId);
        return R.ok().message("视频删除成功");
    }

    //根据视频id获取视频凭证
    @GetMapping("getPlayAuth/{videoId}")
    public R getPlayAuth(@ApiParam(name = "videoId", value = "云端视频id", required = true)
                             @PathVariable String videoId) throws ClientException {

        String playAuth = videoService.getPlayAuthById(videoId);
        if(playAuth!=null){
            return R.ok().message("获取凭证成功").data("playAuth",playAuth);
        }else{
            return R.error().message("获取视频凭证失败！");
        }
    }
}
