package com.introtoc.introService.controller;


import com.introtoc.commonUtils.R;
import com.introtoc.introService.entity.Teacher;
import com.introtoc.introService.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tengsss
 * @since 2021-03-31
 */
@Api(description="教师管理")
@RestController
@RequestMapping("/introService/teacher")
@CrossOrigin
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    //查询教师信息  学生端【个人首页】
    @ApiOperation(value="查询教师信息")
    @GetMapping("/getTeacherInfo/{teacherId}")
    public R getTeacherInfo(@PathVariable String teacherId){
        //默认系统只需要一个老师，所以在学生数据库中没有设置学生教师对应关系
        //默认设置教师id为唯一教师id 0236726736254739472
        String id = "0236726736254739472";
        Teacher teacher = teacherService.getTeacherById(id);
        return R.ok().data("teacher",teacher);
    }

    //更新教师信息  教师端【教师个人信息】
    @ApiOperation(value = "更新教师信息")
    @PostMapping("/updateTeacher")
    public R updateTeacher(@RequestBody Teacher teacher){
        teacher.setGmtModified(new Date()); //设置更新时间
        boolean flag = teacherService.updateById(teacher);
        return flag?R.ok():R.error();
    }


}

