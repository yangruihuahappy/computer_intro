package com.introtoc.introService.entity.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel(value = "待办事项显示对象", description = "待办事项显示对象")
@Data
public class ToDoQuery {

    @ApiModelProperty(value = "对象名称")
    private String title;

    @ApiModelProperty(value = "类别")
    private String type;

    @ApiModelProperty(value = "查询创建时间", example = "2019-01-01 10:10:10")
    private String begin;//注意，这里使用的是String类型，前端传过来的数据无需进行类型转换

    @ApiModelProperty(value = "查询截止时间", example = "2019-12-01 10:10:10")
    private String end;

    @ApiModelProperty(value = "剩余时间",example = "1")
    private long remaining;
}
