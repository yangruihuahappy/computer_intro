package com.introtoc.introService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.introtoc.introService.entity.Preview;
import com.introtoc.introService.entity.StuPreview;
import com.introtoc.introService.mapper.StuPreviewMapper;
import com.introtoc.introService.service.StuPreviewService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-08
 */
@Service
public class StuPreviewServiceImpl extends ServiceImpl<StuPreviewMapper, StuPreview> implements StuPreviewService {

    //根据学生id查找学生预习情况
    @Override
    public List<StuPreview> listByStuId(String stuId) {
        //构建条件
        QueryWrapper<StuPreview> wrapper = new QueryWrapper<>();
        wrapper.eq("stu_id", stuId);
        return baseMapper.selectList(wrapper);
    }

    //根据id查询
    @Override
    public StuPreview getInfoById(String id) {//传入id为练习id
        return baseMapper.selectById(id);
    }

    //根据前端传过来的信息查找当前题目
    @Override
    public StuPreview getPreview(StuPreview stuPreview) {
        QueryWrapper<StuPreview> wrapper = new QueryWrapper<>();
        wrapper.eq("id", stuPreview.getId());
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public Integer findScore(Preview preview,String stuId) {
        System.out.println("传过来的preview"+preview.toString());
        QueryWrapper<StuPreview> wrapper = new QueryWrapper<>();
        wrapper.eq("stu_id",stuId);
        wrapper.eq("preview_id",preview.getId());
        if(baseMapper.selectOne(wrapper)!=null){
            return baseMapper.selectOne(wrapper).getScore();
        }else{
            return 0;
        }

    }

    @Override
    public int findFinishCount(Preview p) {
        QueryWrapper<StuPreview> wrapper = new QueryWrapper<>();
        wrapper.eq("preview_id",p.getId());
        wrapper.eq("finish",1);
        return baseMapper.selectList(wrapper).size();
    }

    @Override
    public int findAllCount(Preview p) {
        QueryWrapper<StuPreview> wrapper = new QueryWrapper<>();
        wrapper.eq("preview_id",p.getId());
        return baseMapper.selectList(wrapper).size();
    }
}
