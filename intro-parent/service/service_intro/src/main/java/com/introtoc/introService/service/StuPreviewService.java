package com.introtoc.introService.service;

import com.introtoc.introService.entity.Preview;
import com.introtoc.introService.entity.StuPreview;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tengsss
 * @since 2021-04-08
 */
public interface StuPreviewService extends IService<StuPreview> {

    //根据学生id查找学生预习列表
    List<StuPreview> listByStuId(String stuId);

    //根据id查询练习
    StuPreview getInfoById(String id);

    //根据前端传过来的信息查找当前题目
    StuPreview getPreview(StuPreview stuPreview);

    Integer findScore(Preview preview,String stuId);

    int findFinishCount(Preview p);

    int findAllCount(Preview p);
}
