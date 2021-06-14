package com.introtoc.introService.controller;

import com.introtoc.commonUtils.JwtUtils;
import com.introtoc.commonUtils.R;
import com.introtoc.introService.entity.Student;
import com.introtoc.introService.entity.Teacher;
import com.introtoc.introService.entity.login.LoginForm;
import com.introtoc.introService.service.StudentService;
import com.introtoc.introService.service.TeacherService;
import com.introtoc.serviceBase.exceptionHandler.IntroException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Api(description = "登陆管理")
@RestController
@RequestMapping("/introService/user")
@CrossOrigin //解决跨域
public class IntroLoginController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;

    //login 登陆
    @ApiOperation(value = "用户登录")
    @PostMapping("login")
    public R login(@RequestBody LoginForm loginInfo) {
        if (loginInfo.getUsername().length() == 13) { //登陆账号为13位为学生 6位为教师
            String token = studentService.login(loginInfo);
            System.out.println("看一下token============" + token);
            return R.ok().data("token", token).data("admin", false);
        }
        //判断是管理员
        else if (loginInfo.getUsername().length() == 6) {
            String token = teacherService.login(loginInfo);
            System.out.println("看一下token============" + token);
            return R.ok().data("token", token).data("admin", true);
        }else{
            return R.error().message("输入的用户名有误");
        }

    }

    @ApiOperation(value = "根据token获取学生登录信息")
    @GetMapping("getLoginInfo")
    public R getLoginInfo(HttpServletRequest request) {
        try {
            String userId = JwtUtils.getMemberIdByJwtToken(request);
            System.out.println("id为" + userId);
            if(request.getHeader("admin").equals("false")){
                LoginForm loginInfo = studentService.getLoginInfo(userId);
                Student stuInfo = studentService.getStuInfoById(userId);
                System.out.println("学生信息为：" + stuInfo.toString());
                return R.ok().data("item", loginInfo).data("roles", stuInfo).data("name", stuInfo.getName()).data("avatar", stuInfo.getAvatar());
            }else{
                LoginForm loginInfo = teacherService.getLoginInfo(userId);
                Teacher teacher = teacherService.getTeacherById(userId);
                System.out.println("教师信息为：" + teacher.toString());
                return R.ok().data("item", loginInfo).data("roles", teacher).data("name", teacher.getName()).data("avatar", teacher.getAvatar());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IntroException(20001, "token获取登录信息失败");
        }
    }

//    @ApiOperation(value = "根据token获取教师登录信息")
//    @GetMapping("getTeaLoginInfo")
//    public R getTeaLoginInfo(HttpServletRequest request) {
//        try {
//
//            String teacherId = JwtUtils.getMemberIdByJwtToken(request);
//            System.out.println("id为" + teacherId);
//
//            LoginForm loginInfo = teacherService.getLoginInfo(teacherId);
//            Teacher teacher = teacherService.getTeacherById(teacherId);
//            System.out.println("教师信息为：" + teacher.toString());
//            return R.ok().data("item", loginInfo).data("roles", teacher).data("name", teacher.getName()).data("avatar", teacher.getAvatar());
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new IntroException(20001, "token获取登录信息失败");
//        }
//    }

    //logout 登出
    @ApiOperation(value = "用户退出登陆")
    @PostMapping("logout")
    public R login() {
        return R.ok().data("token", "");
    }

    //info 测试数据
    @GetMapping("info")
    public R info() {
        return R.ok().data("roles", "[admin]").data("name", "admin").data("avatar", "https://img-pre.ivsky.com/img/tupian/pre/202007/17/beijixiong-001.jpg");
    }


}
