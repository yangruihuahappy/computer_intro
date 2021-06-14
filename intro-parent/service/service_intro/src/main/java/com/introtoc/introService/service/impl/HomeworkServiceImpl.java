package com.introtoc.introService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.introtoc.introService.entity.Homework;
import com.introtoc.introService.entity.Preview;
import com.introtoc.introService.mapper.HomeworkMapper;
import com.introtoc.introService.service.HomeworkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-12
 */
@Service
public class HomeworkServiceImpl extends ServiceImpl<HomeworkMapper, Homework> implements HomeworkService {

    //查找所有分数
    @Override
    public List<Integer> findScore(String stuId) {
        return null;
    }

    @Override
    public Homework findHomework(String stuId, String sectionId) {
        QueryWrapper<Homework> wrapperHome = new QueryWrapper<>();
        wrapperHome.eq("section_id",sectionId);
        return baseMapper.selectOne(wrapperHome);
    }
}
