package com.introtoc.introService.entity.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "学生查询对象", description = "学生查询对象")
@Data
public class StudentQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "学生学号")
    private String stuNum;

    @ApiModelProperty(value = "学生姓名")
    private String name;
}

