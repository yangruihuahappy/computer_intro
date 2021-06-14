package com.introtoc.introService.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.introtoc.introService.entity.Student;
import com.introtoc.introService.entity.excel.StudentDate;
import com.introtoc.introService.service.StudentService;
import com.introtoc.serviceBase.exceptionHandler.IntroException;

import java.util.Date;

public class StudentExcelListener extends AnalysisEventListener<StudentDate> {
    //因为StudentExcelListener 不能交给spring进行管理，需要自己new不能注入其他对象
    //不能实现数据库操作
    private StudentService studentService;

    //构造函数
    public StudentExcelListener(StudentService studentService){
        this.studentService = studentService;
    }
    public StudentExcelListener(){
    }


    //读取excel内容 一行一行进行读取
    @Override
    public void invoke(StudentDate studentDate, AnalysisContext analysisContext) {
        if(studentDate == null){
            throw new IntroException(20001,"文件数据为空");
        }

        //传递过来的数据只有四个值，但是需要存到数据库的除了默认为空值的字段之外还需要设置id、password、enthusiasm、gmtCreate、gmtModified
        Student student = new Student();
        student.setStuNum(studentDate.getStuNum());
        student.setName(studentDate.getName());
        student.setPassword(studentDate.getStuNum()); //默认初始密码为学号。学生登陆需要自己修改
        student.setClasses(studentDate.getClasses());
        student.setGrade(studentDate.getGrade());
        student.setEnthusiasm((long) 0);
        student.setGmtCreate(new Date());
        student.setGmtModified(new Date());

        studentService.save(student);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
