package com.introtoc.introService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.introtoc.introService.entity.Homework;
import com.introtoc.introService.entity.StuHomework;
import com.introtoc.introService.entity.StuPreview;
import com.introtoc.introService.mapper.StuHomeworkMapper;
import com.introtoc.introService.service.StuHomeworkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-12
 */
@Service
public class StuHomeworkServiceImpl extends ServiceImpl<StuHomeworkMapper, StuHomework> implements StuHomeworkService {

    //根据id查询练习详细信息
    @Override
    public StuHomework getInfoById(String id) {
        return baseMapper.selectById(id);
    }

    //根据前端传过来的信息查找当前题目
    @Override
    public StuHomework getHomework(StuHomework stuHomework) {
        QueryWrapper<StuHomework> wrapper = new QueryWrapper<>();
        wrapper.eq("id",stuHomework.getId());
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public Integer findScore(Homework homework, String stuId) {
        System.out.println("传过来的preview"+homework.toString());
        QueryWrapper<StuHomework> wrapper = new QueryWrapper<>();
        wrapper.eq("stu_id",stuId);
        wrapper.eq("homework_id",homework.getId());
        if(baseMapper.selectOne(wrapper)!=null){
            return baseMapper.selectOne(wrapper).getScore();
        }else{
            return 0;
        }
    }

    @Override
    public int findFinishCount(Homework h) {
        QueryWrapper<StuHomework> wrapper = new QueryWrapper<>();
        wrapper.eq("homework_id",h.getId());
        wrapper.eq("finish",1);
        return baseMapper.selectList(wrapper).size();
    }

    @Override
    public int findAllCount(Homework h) {
        QueryWrapper<StuHomework> wrapper = new QueryWrapper<>();
        wrapper.eq("homework_id",h.getId());
        return baseMapper.selectList(wrapper).size();
    }
}
