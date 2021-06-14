package com.introtoc.introService.service;

import com.introtoc.introService.entity.StuTimedtest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.introtoc.introService.entity.Timedtest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-14
 */
public interface StuTimedtestService extends IService<StuTimedtest> {

    //根据前端传过来的信息查找当前题目
    StuTimedtest getTimedtest(StuTimedtest stuTimedtest);

    Integer findScore(Timedtest timedtest, String stuId);

    int findFinishCount(Timedtest t);

    int findAllCount(Timedtest t);
}
