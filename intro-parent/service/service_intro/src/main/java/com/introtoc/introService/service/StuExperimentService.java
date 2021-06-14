package com.introtoc.introService.service;

import com.introtoc.introService.entity.Experiment;
import com.introtoc.introService.entity.StuExperiment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-15
 */
public interface StuExperimentService extends IService<StuExperiment> {

    Integer findScore(Experiment experiment, String stuId);

    int findFinishCount(Experiment e);

    int findAllCount(Experiment e);
}
