package com.introtoc.introService.service;

import com.introtoc.introService.entity.Preview;
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
public interface PreviewService extends IService<Preview> {

    //保存信息并返回id
    String saveAndReturnId(Preview preview);

    //查找指定练习
    Preview findPreview(String stuId, String id);

}
