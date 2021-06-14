package com.introtoc.introService.service;

import com.introtoc.introService.entity.Experiment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-15
 */
public interface ExperimentService extends IService<Experiment> {

    // 逻辑删除
    boolean removeExperiment(String id);

    //查找所有分数
    List<Integer> findScore(String stuId);

    Experiment findExperiment(String stuId, String id);
}
