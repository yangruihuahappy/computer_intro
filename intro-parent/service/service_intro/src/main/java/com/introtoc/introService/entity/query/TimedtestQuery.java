package com.introtoc.introService.entity.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "教师端的随堂测验查询对象", description = "教师端的随堂测验查询对象")
@Data
public class TimedtestQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "标题内容，模糊查询")
    private String title;

    @ApiModelProperty(value = "章节")
    private String chapter;

    @ApiModelProperty(value = "小节")
    private String section;

    @ApiModelProperty(value = "查询开始时间", example = "2019-01-01 10:10:10")
    private String begin;//注意，这里使用的是String类型，前端传过来的数据无需进行类型转换

    @ApiModelProperty(value = "查询截止时间", example = "2019-12-01 10:10:10")
    private String end;

}