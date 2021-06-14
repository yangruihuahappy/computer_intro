package com.introtoc.introService.service;

import com.introtoc.introService.entity.Homework;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-12
 */
public interface HomeworkService extends IService<Homework> {

    //查找所有分数
    List<Integer> findScore(String stuId);

    Homework findHomework(String stuId, String id);
}
