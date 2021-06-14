package com.introtoc.introService.service;

import com.introtoc.introService.entity.Experiment;
import com.introtoc.introService.entity.Timedtest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-14
 */
public interface TimedtestService extends IService<Timedtest> {

    //查找所有分数
    List<Integer> findScore(String stuId);

    Timedtest findTimedtest(String stuId, String id);

}
