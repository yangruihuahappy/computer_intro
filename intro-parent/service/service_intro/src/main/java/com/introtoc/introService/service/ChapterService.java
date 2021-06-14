package com.introtoc.introService.service;

import com.introtoc.introService.entity.Chapter;
import com.baomidou.mybatisplus.extension.service.IService;
import com.introtoc.introService.entity.chapter.OneChapter;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tengsss
 * @since 2021-03-31
 */
public interface ChapterService extends IService<Chapter> {

    //获取所有章节信息 【知识点】目录 将所有小节信息封装到章节中
    List<OneChapter> getAllOneAndTwoChapter();

}
