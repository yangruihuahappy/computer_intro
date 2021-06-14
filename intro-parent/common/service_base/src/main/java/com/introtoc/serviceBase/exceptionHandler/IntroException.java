package com.introtoc.serviceBase.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义异常类
 */
@Data
@AllArgsConstructor //生成有参数的构造方法
@NoArgsConstructor //生成无参的构造方法
public class IntroException extends RuntimeException {
    private Integer code; //状态码
    private String msg; //异常信息
}
