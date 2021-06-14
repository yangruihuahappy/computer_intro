package com.introtoc.videoService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

//默认不加载数据库
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableFeignClients //Nacos客户端
@ComponentScan(basePackages = {"com.introtoc"})

public class VideoApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoApplication.class, args);
    }
}
