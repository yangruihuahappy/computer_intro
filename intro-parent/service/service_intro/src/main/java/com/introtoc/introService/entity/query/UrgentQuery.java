package com.introtoc.introService.entity.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "紧急事项显示对象", description = "紧急事项显示对象")
@Data
public class UrgentQuery {

    @ApiModelProperty(value = "学生id")
    private String stuId;

    @ApiModelProperty(value = "类别")
    private String type;

}
