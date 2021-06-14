package com.introtoc.introService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.introtoc.introService.entity.StuExperiment;
import com.introtoc.introService.entity.StuHomework;
import com.introtoc.introService.entity.StuTimedtest;
import com.introtoc.introService.entity.Timedtest;
import com.introtoc.introService.mapper.StuTimedtestMapper;
import com.introtoc.introService.service.StuTimedtestService;
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
public class StuTimedtestServiceImpl extends ServiceImpl<StuTimedtestMapper, StuTimedtest> implements StuTimedtestService {

    //根据前端传过来的信息查找当前题目
    @Override
    public StuTimedtest getTimedtest(StuTimedtest stuTimedtest) {
        QueryWrapper<StuTimedtest> wrapper = new QueryWrapper<>();
        wrapper.eq("id",stuTimedtest.getId());
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public Integer findScore(Timedtest timedtest, String stuId) {
        System.out.println("传过来的preview"+timedtest.toString());
        QueryWrapper<StuTimedtest> wrapper = new QueryWrapper<>();
        wrapper.eq("stu_id",stuId);
        wrapper.eq("timedtest_id",timedtest.getId());
        wrapper.orderByDesc("score");
        List<StuTimedtest> list = baseMapper.selectList(wrapper);
        if(baseMapper.selectOne(wrapper)!=null){
            return list.get(0).getScore();
        }else{
            return 0;
        }
    }

    @Override
    public int findFinishCount(Timedtest t) {
        QueryWrapper<StuTimedtest> wrapper = new QueryWrapper<>();
        wrapper.eq("timedtest_id",t.getId());
        wrapper.eq("finish",1);
        return baseMapper.selectList(wrapper).size();
    }

    @Override
    public int findAllCount(Timedtest t) {
        QueryWrapper<StuTimedtest> wrapper = new QueryWrapper<>();
        wrapper.eq("timedtest_id",t.getId());
        return baseMapper.selectList(wrapper).size();
    }
}
