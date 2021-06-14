package com.introtoc.introService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.introtoc.introService.entity.StuPreview;
import com.introtoc.introService.entity.StuSummary;
import com.introtoc.introService.entity.Summary;
import com.introtoc.introService.mapper.StuSummaryMapper;
import com.introtoc.introService.service.StuSummaryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class StuSummaryServiceImpl extends ServiceImpl<StuSummaryMapper, StuSummary> implements StuSummaryService {

    @Override
    public StuSummary findOne(String summaryId, String stuId) {
        QueryWrapper<StuSummary> wrapper = new QueryWrapper<>();
        wrapper.eq("stu_id",stuId);
        wrapper.eq("summary_id",summaryId);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public int findFinishCount(Summary s) {
        QueryWrapper<StuSummary> wrapper = new QueryWrapper<>();
        wrapper.eq("summary_id",s.getId());
        wrapper.eq("finish",1);
        return baseMapper.selectList(wrapper).size();
    }

    @Override
    public int findAllCount(Summary s) {
        QueryWrapper<StuSummary> wrapper = new QueryWrapper<>();
        wrapper.eq("summary_id",s.getId());
        return baseMapper.selectList(wrapper).size();
    }
}
