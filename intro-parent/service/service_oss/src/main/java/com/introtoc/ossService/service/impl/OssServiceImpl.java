package com.introtoc.ossService.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.introtoc.ossService.service.OssService;
import com.introtoc.ossService.utils.ConstantPropertiesUtil;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {

    //教师上传实验报告模板到oss
    @Override
    public String uploadFileExperiment(MultipartFile file) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = ConstantPropertiesUtil.END_POINT;
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = ConstantPropertiesUtil.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtil.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtil.BUCKET_NAME;

        try {
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            // 上传文件流。 获取上传问价你的输入流
            InputStream inputStream = file.getInputStream();

            //调用OSSClient
            //第一个参数 BucketName
            //第二个参数 上传到oss文件路径和文件名称
            //获取文件名称
            String fileName = file.getOriginalFilename();
            //1 在文件名称里面添加随机唯一的值
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            fileName = uuid + fileName;
            //2 把文件放到baseExperimentReport里
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            System.out.println(suffix);
            //判断是图片还是doc文档 进行拼接
            if (suffix.equals(".doc") || suffix.equals(".docx") || suffix.equals(".pdf")) {
                fileName = "baseExperimentReport/" + fileName;
            } else if (suffix.equals(".jpg") || suffix.equals(".png") || suffix.equals(".jpeg")) {
                fileName = "avatar/" + fileName;
            } else {
                //获取当前日期
                String datePath = new DateTime().toString("yyyy/MM/dd");
                fileName = datePath + "/" + fileName;
            }

            //第三个参数 上传文件的输入流
            ossClient.putObject(bucketName, fileName, inputStream);

            // 关闭OSSClient。
            ossClient.shutdown();

            //上传之后的路径返回
            //需要把上传的阿里云oss路径手动拼接
            //https://edu-tengshan.oss-cn-beijing.aliyuncs.com/66460014F86C9A88F6583F8008A32EC9.jpg
            String url = "https://" + bucketName + "." + endpoint + "/" + fileName;
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //下载文件到本地
    @Override
    public void loadFile(String url) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。根据rel下载
        String endpoint = ConstantPropertiesUtil.END_POINT;
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = ConstantPropertiesUtil.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtil.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtil.BUCKET_NAME;

        // 填写不包含Bucket名称在内的Object完整路径，例如testfolder/exampleobject.txt。
        String objectName = url.substring(url.indexOf("baseExperimentReport"));
        System.out.println(objectName);
//        String objectName = "base/ExperimentBase.docx";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
        // 如果未指定本地路径，则下载后的文件默认保存到示例程序所属项目对应本地路径中。
        ossClient.getObject(new GetObjectRequest(bucketName, objectName), new File("D:\\testLoad\\" + objectName.substring(objectName.lastIndexOf("/"))));

        // 关闭OSSClient。
        ossClient.shutdown();

    }

    @Override
    public void removeFile(String objectName) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = ConstantPropertiesUtil.END_POINT;
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = ConstantPropertiesUtil.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtil.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtil.BUCKET_NAME;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        String suffix = objectName.substring(objectName.lastIndexOf("."));
        System.out.println(objectName);
        if (suffix.equals(".doc") || suffix.equals(".docx") || suffix.equals(".pdf")) {
            objectName = "baseExperimentReport/" + objectName;
        }
        // 删除文件。如需删除文件夹，请将ObjectName设置为对应的文件夹名称。如果文件夹非空，则需要将文件夹下的所有object删除后才能删除该文件夹。
        ossClient.deleteObject(bucketName, objectName);

        // 关闭OSSClient。
        ossClient.shutdown();

    }
}
