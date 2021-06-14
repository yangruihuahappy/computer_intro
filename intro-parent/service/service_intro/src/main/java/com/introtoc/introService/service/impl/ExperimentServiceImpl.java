package com.introtoc.introService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.introtoc.introService.entity.Experiment;
import com.introtoc.introService.entity.StuExperiment;
import com.introtoc.introService.entity.Timedtest;
import com.introtoc.introService.mapper.ExperimentMapper;
import com.introtoc.introService.service.ExperimentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.introtoc.introService.service.StuExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-15
 */
@Service
public class ExperimentServiceImpl extends ServiceImpl<ExperimentMapper, Experiment> implements ExperimentService {

    //注入StuExperiment
    @Autowired
    private StuExperimentService stuExperimentService;

    //逻辑删除
    //删除某一个实验应该删除当下的所有同学的该实验
    @Override
    public boolean removeExperiment(String id) {
        //先删除eduExperiment表中的所有该实验信息
        QueryWrapper<StuExperiment> wrapper = new QueryWrapper<>();
        wrapper.eq("experiment_id", id);
        stuExperimentService.remove(wrapper);
        //再删除Experiment中的信息
        return baseMapper.deleteById(id) != 0;
    }

    //查找所有分数
    @Override
    public List<Integer> findScore(String stuId) {
        return null;
    }

    @Override
    public Experiment findExperiment(String stuId, String sectionId) {
        QueryWrapper<Experiment> wrapperExp = new QueryWrapper<>();
        wrapperExp.eq("section_id", sectionId);
        wrapperExp.orderByDesc("gmt_modified");
        List<Experiment> list = baseMapper.selectList(wrapperExp);
        return list.size() == 0 ? null : baseMapper.selectList(wrapperExp).get(0);
    }
}
