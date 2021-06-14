package com.introtoc.introService.service;

import com.introtoc.introService.entity.Student;
import com.baomidou.mybatisplus.extension.service.IService;
import com.introtoc.introService.entity.login.LoginForm;
import com.introtoc.introService.entity.query.ToDoQuery;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tengsss
 * @since 2021-03-30
 */
public interface StudentService extends IService<Student> {

    //根据id查询学生登录信息  用于主页展示
    Student getStuInfoById(String stuId);

    //批量添加学生信息
    void saveStudentBatch(MultipartFile file, StudentService studentService);

    //找到所有学生id信息
    List<String> findStuIdList();

    //学生登陆返回token
    String login(LoginForm loginInfo);

    LoginForm getLoginInfo(String studentId);

    //用于返回TodoList
    List<ToDoQuery> setToDoList(Student stu);

    //获取所有紧急任务
    List<ToDoQuery> getUrgent(Student student,String type);

    //用于返回MissList
    List<ToDoQuery> setMissedList(Student stu);
}
