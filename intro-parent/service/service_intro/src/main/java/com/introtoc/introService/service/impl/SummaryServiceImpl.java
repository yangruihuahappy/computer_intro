package com.introtoc.introService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.introtoc.introService.entity.StuSummary;
import com.introtoc.introService.entity.Summary;
import com.introtoc.introService.mapper.SummaryMapper;
import com.introtoc.introService.service.StuSummaryService;
import com.introtoc.introService.service.SummaryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-16
 */
@Service
public class SummaryServiceImpl extends ServiceImpl<SummaryMapper, Summary> implements SummaryService {

    @Autowired
    private StuSummaryService stuSummaryService;

    @Override
    public boolean deleteSummary(String id) {
        QueryWrapper<StuSummary> wrapper = new QueryWrapper<>();
        wrapper.eq("summary_id",id);
        stuSummaryService.remove(wrapper);

        return baseMapper.deleteById(id)!=0;
    }
}
