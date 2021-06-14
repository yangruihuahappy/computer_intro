package com.introtoc.introService.service;

import com.introtoc.introService.entity.Summary;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-16
 */
public interface SummaryService extends IService<Summary> {

    boolean deleteSummary(String id);

}
