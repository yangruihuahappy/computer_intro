package com.introtoc.introService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.introtoc.commonUtils.JwtUtils;
import com.introtoc.introService.entity.Student;
import com.introtoc.introService.entity.Teacher;
import com.introtoc.introService.entity.login.LoginForm;
import com.introtoc.introService.mapper.TeacherMapper;
import com.introtoc.introService.service.TeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.introtoc.serviceBase.exceptionHandler.IntroException;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tengsss
 * @since 2021-03-31
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    //查询教师信息  学生端【个人首页】
    @Override
    public Teacher getTeacherById(String teacherId) {
        return baseMapper.selectById(teacherId);
    }

    @Override
    public String login(LoginForm loginInfo) {
        String username = loginInfo.getUsername();
        String password = loginInfo.getPassword();

        //校验参数
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(username)) {
            throw new IntroException(20001, "登录信息错误");
        }

        //获取教师信息
        Teacher teacher = baseMapper.selectOne(new QueryWrapper<Teacher>().eq("username", username));
        //判断教师是否存在等
        if(null == teacher){
            throw new IntroException(20001, "用户名错误");
        }
        //校验用户密码
        if (!password.equals(teacher.getPassword())) {
            throw new IntroException(20001, "密码错误");
        }
        //校验是否被禁用
        if (teacher.getIsDeleted()) {
            throw new IntroException(20001, "学生已被退学");
        }

        //使用JWT生成token字符串  //当心中文问题
        String token = JwtUtils.getJwtToken(teacher.getId(), teacher.getName());
        return token;
    }

    @Override
    public LoginForm getLoginInfo(String teacherId) {
        Teacher teacher = baseMapper.selectById(teacherId);
        LoginForm loginInfo = new LoginForm();
        loginInfo.setUsername(teacher.getUsername());
        loginInfo.setPassword(teacher.getPassword());
        return loginInfo;
    }
}
