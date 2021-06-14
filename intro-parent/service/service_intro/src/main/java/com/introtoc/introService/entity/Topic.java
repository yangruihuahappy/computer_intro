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
 * @since 2021-04-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Topic对象", description="")
public class Topic implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "题目id")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "提供者学生id")
    private String providerId;

    @ApiModelProperty(value = "分类 0选择题 1辨析题 2简答题")
    private Integer type;

    @ApiModelProperty(value = "问题内容")
    private String content;

    @ApiModelProperty(value = "问题选项")
    private String options;

    @ApiModelProperty(value = "答案")
    private String answer;

    @ApiModelProperty(value = "解析")
    private String explaining;

    @ApiModelProperty(value = "所属章节")
    private String chapterId;

    @ApiModelProperty(value = "所属小节")
    private String sectionId;

    @ApiModelProperty(value = "讨论数量")
    private Integer commentCount;

    @ApiModelProperty(value = "0 未采用 1 采用")
    private Boolean adopted;

    @ApiModelProperty(value = "逻辑删除 0未删除 1删除")
    private Boolean isDelete;

    @ApiModelProperty(value = "创建时间")
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
    private Date gmtModified;


}
