package com.introtoc.introService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.introtoc.introService.entity.Experiment;
import com.introtoc.introService.entity.StuExperiment;
import com.introtoc.introService.entity.StuHomework;
import com.introtoc.introService.mapper.StuExperimentMapper;
import com.introtoc.introService.service.StuExperimentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-15
 */
@Service
public class StuExperimentServiceImpl extends ServiceImpl<StuExperimentMapper, StuExperiment> implements StuExperimentService {

    @Override
    public Integer findScore(Experiment experiment, String stuId) {
        System.out.println("传过来的preview"+experiment.toString());
        QueryWrapper<StuExperiment> wrapper = new QueryWrapper<>();
        wrapper.eq("stu_id",stuId);
        wrapper.eq("experiment_id",experiment.getId());
        wrapper.orderByDesc("score");
        List<StuExperiment> list = baseMapper.selectList(wrapper);
        if(baseMapper.selectOne(wrapper)!=null){
            return list.get(0).getScore();
        }else{
            return 0;
        }
    }

    @Override
    public int findFinishCount(Experiment e) {
        QueryWrapper<StuExperiment> wrapper = new QueryWrapper<>();
        wrapper.eq("experiment_id",e.getId());
        wrapper.eq("finish",1);
        return baseMapper.selectList(wrapper).size();
    }

    @Override
    public int findAllCount(Experiment e) {
        QueryWrapper<StuExperiment> wrapper = new QueryWrapper<>();
        wrapper.eq("experiment_id",e.getId());
        return baseMapper.selectList(wrapper).size();
    }
}
