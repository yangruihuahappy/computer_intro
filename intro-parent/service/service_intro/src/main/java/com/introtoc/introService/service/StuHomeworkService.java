package com.introtoc.introService.service;

import com.introtoc.introService.entity.Homework;
import com.introtoc.introService.entity.StuHomework;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-12
 */
public interface StuHomeworkService extends IService<StuHomework> {

    //根据id查询练习详细信息
    StuHomework getInfoById(String id);

    //根据前端传过来的信息查找当前题目
    StuHomework getHomework(StuHomework stuHomework);

    Integer findScore(Homework homework, String stuId);

    int findFinishCount(Homework h);

    int findAllCount(Homework h);
}
