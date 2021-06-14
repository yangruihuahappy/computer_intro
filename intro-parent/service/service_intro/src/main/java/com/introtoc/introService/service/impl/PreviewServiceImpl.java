package com.introtoc.introService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.introtoc.introService.entity.Preview;
import com.introtoc.introService.entity.StuPreview;
import com.introtoc.introService.mapper.PreviewMapper;
import com.introtoc.introService.service.PreviewService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-08
 */
@Service
public class PreviewServiceImpl extends ServiceImpl<PreviewMapper, Preview> implements PreviewService {

    @Override
    public String saveAndReturnId(Preview preview) {

        return String.valueOf(baseMapper.insert(preview));

    }

    //查找所有分数
    @Override
    public Preview findPreview(String stuId, String sectionId) {
        System.out.println(sectionId);
        QueryWrapper<Preview> wrapperPre = new QueryWrapper<>();
        wrapperPre.eq("section_id",sectionId);
        return baseMapper.selectOne(wrapperPre);
    }
}
