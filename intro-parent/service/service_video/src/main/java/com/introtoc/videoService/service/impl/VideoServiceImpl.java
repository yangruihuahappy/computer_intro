package com.introtoc.videoService.service.impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.utils.StringUtils;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.DeleteVideoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.introtoc.serviceBase.exceptionHandler.IntroException;
import com.introtoc.videoService.service.VideoService;
import com.introtoc.videoService.utils.AliyunVideoSDKUtils;
import com.introtoc.videoService.utils.ConstantVideoUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class VideoServiceImpl implements VideoService {

    //上传视频
    @Override
    public String uploadVideoAly(MultipartFile file) {
        //先获取文件
        try {
            InputStream inputStream = file.getInputStream(); //文件输入流
            String originalFilename = file.getOriginalFilename();
            String title = originalFilename.substring(0,originalFilename.lastIndexOf("."));

            UploadStreamRequest request = new UploadStreamRequest(
                    ConstantVideoUtils.ACCESS_KEY_ID, //获取通行id
                    ConstantVideoUtils.ACCESS_KEY_SECRET, //获取通行密码
                    title, //上传文件后显示的名称
                    originalFilename, //上传文件的原始名称
                    inputStream); //文件输入流

            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);

            //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。
            // 其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
            String videoId = response.getVideoId();
            if (!response.isSuccess()) {
                String errorMessage = "阿里云上传错误：" + "code：" + response.getCode() + ", message：" + response.getMessage();
                if(StringUtils.isEmpty(videoId)){
                    throw new IntroException(20001, errorMessage);
                }
            }

            return videoId;

        }catch (IOException e){
            throw new IntroException(20001, "guli vod 服务上传失败");
        }
    }

    //删除视频
    @Override
    public void removeVideo(String videoId) {
        try{
            //初始化对象
            DefaultAcsClient client = AliyunVideoSDKUtils.initVodClient(
                    ConstantVideoUtils.ACCESS_KEY_ID,
                    ConstantVideoUtils.ACCESS_KEY_SECRET);

            //创建删除视频的request对象
            DeleteVideoRequest request = new DeleteVideoRequest();
            //设置id
            request.setVideoIds(videoId);

            DeleteVideoResponse response = client.getAcsResponse(request);

            System.out.print("RequestId = " + response.getRequestId() + "\n");

        }catch (ClientException e){
            throw new IntroException(20001, "视频删除失败");
        }
    }

    //根据视频id获取视频凭证
    @Override
    public String getPlayAuthById(String videoId) throws ClientException {
        //根据视频id获取视频播放地址
        //1 创建初始化对象
        DefaultAcsClient client = AliyunVideoSDKUtils.initVodClient("LTAI4G443e87oLwDaTGkkMBr","gQdQXnTXB7ywh21o15FB9HQvyrG9tq");

        //2 创建获取视频凭证的request和response
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        GetVideoPlayAuthResponse response = new GetVideoPlayAuthResponse();

        //3 向request对象里面设置视频id
        request.setVideoId(videoId);
        request.setAuthInfoTimeout(200L);
        //4 调用初始化对象里面的方法，传递request，获取视频凭证
        response = client.getAcsResponse(request);
        System.out.println(response.getPlayAuth());
        return response.getPlayAuth();
    }
}
