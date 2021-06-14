package com.introtoc.introService.service;

import com.introtoc.introService.entity.Knowledge;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tengsss
 * @since 2021-03-31
 */
public interface KnowledgeService extends IService<Knowledge> {

    //根据id查询知识点
    Knowledge getKnowInfoById(String id);
}
