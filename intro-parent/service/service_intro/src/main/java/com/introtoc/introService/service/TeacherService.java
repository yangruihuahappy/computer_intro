package com.introtoc.introService.service;

import com.introtoc.introService.entity.Teacher;
import com.baomidou.mybatisplus.extension.service.IService;
import com.introtoc.introService.entity.login.LoginForm;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tengsss
 * @since 2021-03-31
 */
public interface TeacherService extends IService<Teacher> {

    //查询教师信息  学生端【个人首页】
    Teacher getTeacherById(String teacherId);

    String login(LoginForm loginInfo);

    LoginForm getLoginInfo(String teacherId);
}
