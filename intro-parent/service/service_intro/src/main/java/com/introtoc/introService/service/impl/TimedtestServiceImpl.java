package com.introtoc.introService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.introtoc.introService.entity.Homework;
import com.introtoc.introService.entity.Timedtest;
import com.introtoc.introService.mapper.TimedtestMapper;
import com.introtoc.introService.service.TimedtestService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-14
 */
@Service
public class TimedtestServiceImpl extends ServiceImpl<TimedtestMapper, Timedtest> implements TimedtestService {

    //查找所有分数
    @Override
    public List<Integer> findScore(String stuId) {
        return null;
    }

    @Override
    public Timedtest findTimedtest(String stuId, String sectionId) {
        QueryWrapper<Timedtest> wrapperTime = new QueryWrapper<>();
        wrapperTime.eq("section_id",sectionId);
        wrapperTime.orderByDesc("gmt_modified");
        List<Timedtest> list = baseMapper.selectList(wrapperTime);
        return list.size()==0?null : baseMapper.selectList(wrapperTime).get(0);
    }
}
