package com.introtoc.introService.service;

import com.introtoc.introService.entity.StuSummary;
import com.baomidou.mybatisplus.extension.service.IService;
import com.introtoc.introService.entity.Summary;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-16
 */
public interface StuSummaryService extends IService<StuSummary> {

    StuSummary findOne(String id, String stuId);

    int findFinishCount(Summary s);

    int findAllCount(Summary s);
}
