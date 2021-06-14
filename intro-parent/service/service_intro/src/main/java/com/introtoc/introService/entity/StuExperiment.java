package com.introtoc.introService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
 * @since 2021-04-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StuExperiment对象", description="")
public class StuExperiment implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "学生实验练习对应关系")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "学生id")
    private String stuId;

    @ApiModelProperty(value = "实验练习id")
    private String experimentId;

    @ApiModelProperty(value = "学生实验报告地址")
    private String ossUrl;

    @ApiModelProperty(value = "学生成绩")
    private Integer score;

    @ApiModelProperty(value = "是否完成")
    private Boolean finish;

    @ApiModelProperty(value = "逻辑删除")
    private Boolean isDeleted;

    @ApiModelProperty(value = "截止时间")
    private Date gmtDeadline;

    @ApiModelProperty(value = "完成时间")
    private Date gmtFinish;


}
