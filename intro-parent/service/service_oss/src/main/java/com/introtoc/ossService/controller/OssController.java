package com.introtoc.ossService.controller;


import com.introtoc.commonUtils.R;
import com.introtoc.ossService.service.OssService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/introOss/fileOss")
@CrossOrigin
public class OssController {

    @Autowired
    private OssService ossService;

    //教师上传实验报告模板
    @PostMapping("/uploadFile")
    public R uploadFileExperiment(@RequestBody MultipartFile file){
        //获取上传的文件名称
        String url =ossService.uploadFileExperiment(file);
        return R.ok().data("url",url);
    }

    //云端删除文件
    @PostMapping("removeByUrl")
    public R removeVideo(@ApiParam(name = "fileUrl", value = "云端文件地址", required = true)@RequestBody String fileUrl) {
        //先要对fileUrl进行预处理 需要将fileUrl变换成objectName 即id+name
        if(fileUrl.charAt(fileUrl.length()-1)=='='){
            fileUrl = fileUrl.substring(0,fileUrl.length()-1);
            fileUrl = fileUrl.replaceAll("%2F", "/");
            fileUrl = fileUrl.replaceAll("%3A", ":");
        }
        System.out.println(fileUrl);
        String objectName = fileUrl.substring(fileUrl.lastIndexOf('/')+1);
        System.out.println(objectName);
        ossService.removeFile(objectName);
        return R.ok().message("文件删除成功");
    }

    //下载示例模板 根据url下载
    @PostMapping("/downloadBaseFileByUrl")
    public R loadOssFile(@RequestBody String fileUrl){
        //前端传过来的字符串数据会在末尾自动添加一个=符号
        if(fileUrl.charAt(fileUrl.length()-1)=='='){
            fileUrl = fileUrl.substring(0,fileUrl.length()-1);
            fileUrl = fileUrl.replaceAll("%2F", "/");
            fileUrl = fileUrl.replaceAll("%3A", ":");
        }
        System.out.println(fileUrl);
        ossService.loadFile(fileUrl);
        return R.ok();
    }
}
