package com.introtoc.introService.entity.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "每周总结查询对象", description = "每周总结对象")
@Data
public class SummaryQuery {

    private static final long serialVersionUID = 1L;

    //一般来说条件查询时只需要设置好起始时间，找到在期间的每周总结即可，前端传到后端的start和end为一周

    @ApiModelProperty(value = "标题内容，模糊查询")
    private String title;

    @ApiModelProperty(value = "查询开始时间", example = "2019-01-01 10:10:10")
    private String begin;//注意，这里使用的是String类型，前端传过来的数据无需进行类型转换

    @ApiModelProperty(value = "查询截止时间", example = "2019-12-01 10:10:10")
    private String end;
}
