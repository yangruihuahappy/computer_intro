package com.introtoc.introService.service.impl;

import com.introtoc.introService.entity.Knowledge;
import com.introtoc.introService.mapper.KnowledgeMapper;
import com.introtoc.introService.service.KnowledgeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tengsss
 * @since 2021-03-31
 */
@Service
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeMapper, Knowledge> implements KnowledgeService {

    //根据id查询知识点
    @Override
    public Knowledge getKnowInfoById(String id) {
        Knowledge knowledge = baseMapper.selectById(id);
        return knowledge;
    }
}
