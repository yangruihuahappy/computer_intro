package com.introtoc.introService.service;

import com.introtoc.introService.entity.Topic;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-02
 */
public interface TopicService extends IService<Topic> {

    //根据提供者id进行查询题目
    List<Topic> getTopicListByProviderId(String providerId);

    //根据题目章节和小节id进行查询
    List<Topic> findAllTopicByChapterAndSection(String chapterId, String sectionId);
}
