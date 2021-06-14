package com.introtoc.introService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author tengsss
 * @since 2021-04-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StuSummary对象", description="")
public class StuSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "学生-总结关系表")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "学生id")
    private String stuId;

    @ApiModelProperty(value = "总结id")
    private String summaryId;

    @ApiModelProperty(value = "总结内容")
    private String content;

    @ApiModelProperty(value = "是否完成")
    private Boolean finish;

    @ApiModelProperty(value = "总结得分")
    private Integer score;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "完成时间")
    private Date gmtFinish;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "截止日期")
    private Date gmtDeadline;


}
