package com.introtoc.introService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.introtoc.introService.entity.Topic;
import com.introtoc.introService.mapper.TopicMapper;
import com.introtoc.introService.service.TopicService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-02
 */
@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

    //根据提供者id进行查询题目
    @Override
    public List<Topic> getTopicListByProviderId(String providerId) {
        //新建查询条件
        QueryWrapper<Topic> wrapperTopic = new QueryWrapper<>();
        wrapperTopic.eq("provider_id ",providerId);
        return baseMapper.selectList(wrapperTopic);
    }

    @Override
    public List<Topic> findAllTopicByChapterAndSection(String chapterId, String sectionId) {
        QueryWrapper<Topic> wrapper = new QueryWrapper<>();
        wrapper.eq("chapter_id",chapterId);
        wrapper.eq("section_id",sectionId);
        wrapper.eq("adopted",1);
        return baseMapper.selectList(wrapper);
    }
}
