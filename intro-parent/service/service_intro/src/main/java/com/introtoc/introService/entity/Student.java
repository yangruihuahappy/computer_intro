package com.introtoc.introService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * @since 2021-03-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Student对象", description="")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "学生id")
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "学生学号")
    private String stuNum;

    @ApiModelProperty(value = "学生姓名")
    private String name;

    @ApiModelProperty(value = "学生班级")
    private String classes;

    @ApiModelProperty(value = "学生年级")
    private String grade;

    @ApiModelProperty(value = "学生小组，课堂分配")
    private String groupId;

    @ApiModelProperty(value = "登陆密码")
    private String password;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "学生邮箱")
    private String email;

    @ApiModelProperty(value = "学生电话")
    private String telephone;

    @ApiModelProperty(value = "学生技能简介")
    private String skill;

    @ApiModelProperty(value = "积极度，作为平时成绩参考")
    private Long enthusiasm;

    @ApiModelProperty(value = "逻辑删除 1(true)已删除  0(false)未删除")
    @TableLogic
    private Boolean isDeleted;

    @ApiModelProperty(value = "创建时间")
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
    private Date gmtModified;


}
