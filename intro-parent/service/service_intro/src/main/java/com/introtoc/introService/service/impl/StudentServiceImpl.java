package com.introtoc.introService.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.introtoc.commonUtils.JwtUtils;
import com.introtoc.introService.entity.*;
import com.introtoc.introService.entity.excel.StudentDate;
import com.introtoc.introService.entity.login.LoginForm;
import com.introtoc.introService.entity.query.ToDoQuery;
import com.introtoc.introService.listener.StudentExcelListener;
import com.introtoc.introService.mapper.StudentMapper;
import com.introtoc.introService.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.introtoc.serviceBase.exceptionHandler.IntroException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author tengsss
 * @since 2021-03-30
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {


    @Autowired
    private StuPreviewService stuPreviewService;
    @Autowired
    private PreviewService previewService;
    @Autowired
    private StuHomeworkService stuHomeworkService;
    @Autowired
    private HomeworkService homeworkService;
    @Autowired
    private StuTimedtestService stuTimedtestService;
    @Autowired
    private TimedtestService timedtestService;
    @Autowired
    private StuExperimentService stuExperimentService;
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private StuSummaryService stuSummaryService;
    @Autowired
    private SummaryService summaryService;

    //根据id查询学生登录信息  用于主页展示
    @Override
    public Student getStuInfoById(String stuId) {
        return baseMapper.selectById(stuId);
    }

    //批量添加学生信息
    @Override
    public void saveStudentBatch(MultipartFile file, StudentService studentService) {
        try {
            //获取文件输入流
            InputStream in = file.getInputStream();
            //调用EasyExcel方法进行读取
            // 需要先完成ExcelListener的编写
            EasyExcel.read(in, StudentDate.class, new StudentExcelListener(studentService)).sheet().doRead();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> findStuIdList() {
        //查找所有学生信息，封装好id到一个list中 教师发布任务用到
        System.out.println("运行到了查找所有学生id");
        List<String> stuIdList = new ArrayList<>();
        List<Student> studentList = baseMapper.selectList(null);
        System.out.println(studentList.toString());
        for (Student student : studentList) {
            stuIdList.add(student.getId());
        }
        return stuIdList;
    }

    //学生登陆返回token
    @Override
    public String login(LoginForm loginInfo) {
        String username = loginInfo.getUsername();
        String password = loginInfo.getPassword();

        //校验参数
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(username)) {
            throw new IntroException(20001, "登录信息错误");
        }
        //获取用户 username就是学号
        System.out.println("username:      "+username);
        Student student = baseMapper.selectOne(new QueryWrapper<Student>().eq("stu_num", username));
        if (null == student) {
            throw new IntroException(20001, "学号错误");
        }
        //校验用户密码
        if (!password.equals(student.getPassword())) {
            throw new IntroException(20001, "密码错误");
        }

        //校验是否被禁用
        if (student.getIsDeleted()) {
            throw new IntroException(20001, "学生已被退学");
        }

        //使用JWT生成token字符串  //当心中文问题
        String token = JwtUtils.getJwtToken(student.getId(), student.getName());
        return token;

    }

    @Override
    public LoginForm getLoginInfo(String studentId) {
        Student student = baseMapper.selectById(studentId);
        LoginForm loginInfo = new LoginForm();
        loginInfo.setUsername(student.getStuNum());
        loginInfo.setPassword(student.getPassword());
        return loginInfo;
    }

    //用于返回TodoList
    @Override
    public List<ToDoQuery> setToDoList(Student stu) {

        List<ToDoQuery> todoList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //先将未完成的预习放到list
        QueryWrapper<StuPreview> wrapperPre = new QueryWrapper<>();
        wrapperPre.eq("stu_id", stu.getId());
        wrapperPre.eq("finish", 0);
        wrapperPre.ge("gmt_deadline", new Date());
        List<StuPreview> stuPreviews = stuPreviewService.list(wrapperPre);
        System.out.println("stuPreviews:");
        System.out.println(stuPreviews.toString());
        for (StuPreview s : stuPreviews) {
            Preview preview = previewService.getById(s.getPreviewId());
            ToDoQuery toDo = new ToDoQuery();
            toDo.setBegin(sdf.format(preview.getGmtCreate()));
            toDo.setEnd(sdf.format(preview.getGmtDeadline()));
            toDo.setTitle(preview.getTitle());
            toDo.setType("Preview");
            todoList.add(toDo);
        }

        //再将未完成的家庭作业放到list
        QueryWrapper<StuHomework> wrapperHome = new QueryWrapper<>();
        wrapperHome.eq("stu_id", stu.getId());
        wrapperHome.eq("finish", 0);
        wrapperHome.ge("gmt_deadline", new Date());
        List<StuHomework> stuHomeworks = stuHomeworkService.list(wrapperHome);
        System.out.println("stuHomeworks:");
        System.out.println(stuHomeworks.toString());
        for (StuHomework s : stuHomeworks) {
            Homework homework = homeworkService.getById(s.getHomeworkId());
            ToDoQuery toDo = new ToDoQuery();
            toDo.setBegin(sdf.format(homework.getGmtCreate()));
            toDo.setEnd(sdf.format(homework.getGmtDeadline()));
            toDo.setTitle(homework.getTitle());
            toDo.setType("Homework");
            todoList.add(toDo);
        }

        //再将未完成的timedTest放到list
        QueryWrapper<StuTimedtest> wrapperTime = new QueryWrapper<>();
        wrapperTime.eq("stu_id", stu.getId());
        wrapperTime.eq("finish", 0);
        wrapperTime.ge("gmt_deadline", new Date());
        List<StuTimedtest> stuTimedtests = stuTimedtestService.list(wrapperTime);
        System.out.println("timedtest：");
        System.out.println(stuTimedtests.toString());
        for (StuTimedtest s : stuTimedtests) {
            Timedtest timedtest = timedtestService.getById(s.getTimedtestId());
            ToDoQuery toDo = new ToDoQuery();
            toDo.setBegin(sdf.format(timedtest.getGmtCreate()));
            toDo.setEnd(sdf.format(timedtest.getGmtDeadline()));
            toDo.setTitle(timedtest.getTitle());
            toDo.setType("Timedtest");
            todoList.add(toDo);
        }
        //再将未完成的实验练习放到list
        QueryWrapper<StuExperiment> wrapperExp = new QueryWrapper<>();
        wrapperExp.eq("stu_id", stu.getId());
        wrapperExp.eq("finish", 0);
        wrapperExp.ge("gmt_deadline", new Date());
        List<StuExperiment> stuExperiments = stuExperimentService.list(wrapperExp);
        System.out.println("stuExperiments：");
        System.out.println(stuExperiments.toString());
        for (StuExperiment s : stuExperiments) {
            Experiment experiment = experimentService.getById(s.getExperimentId());
            ToDoQuery toDo = new ToDoQuery();
            toDo.setBegin(sdf.format(experiment.getGmtCreate()));
            toDo.setEnd(sdf.format(experiment.getGmtDeadline()));
            toDo.setTitle(experiment.getTitle());
            toDo.setType("Experiment");
            todoList.add(toDo);
        }
        //再将未完成的家庭作业放到list
        QueryWrapper<StuSummary> wrapperSum = new QueryWrapper<>();
        wrapperSum.eq("stu_id", stu.getId());
        wrapperSum.eq("finish", 0);
        wrapperSum.ge("gmt_deadline", new Date());
        List<StuSummary> stuSummaries = stuSummaryService.list(wrapperSum);

        System.out.println("stuSummaries：");
        System.out.println(stuSummaries.toString());
        for (StuSummary s : stuSummaries) {
            Summary summary = summaryService.getById(s.getSummaryId());
            ToDoQuery toDo = new ToDoQuery();
            toDo.setBegin(sdf.format(summary.getGmtCreate()));
            toDo.setEnd(sdf.format(summary.getGmtDeadline()));
            toDo.setTitle(summary.getTitle());
            toDo.setType("Summary");
            todoList.add(toDo);
        }
        //TODO 排序

        return todoList;
    }

    //获取所有课前预习紧急任务
    @Override
    public List<ToDoQuery> getUrgent(Student student,String type) {
        List<ToDoQuery> urgentPreList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //先将未完成的预习放到list
        if(type.equals("Pre")) {
            QueryWrapper<StuPreview> wrapperPre = new QueryWrapper<>();

            wrapperPre.eq("stu_id", student.getId());
            wrapperPre.eq("finish", 0);
            wrapperPre.ge("gmt_deadline", new Date());
            wrapperPre.orderByAsc("gmt_deadline");
            List<StuPreview> stuPreviews = stuPreviewService.list(wrapperPre);
            for (StuPreview s : stuPreviews) {
                System.out.println("查看bug"+s.toString());
                Preview preview = previewService.getById(s.getPreviewId());
                ToDoQuery urgent = new ToDoQuery();
                urgent.setBegin(sdf.format(preview.getGmtCreate()));
                urgent.setEnd(sdf.format(preview.getGmtDeadline()));
                urgent.setTitle(preview.getTitle());
                urgent.setType("Preview");
                try {
                    Date end = sdf.parse(urgent.getEnd());
                    Date now = sdf.parse(sdf.format(new Date()));
                    long days = (end.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
                    urgent.setRemaining(days);
                } catch (Exception e) {
                    throw new IntroException(20001, "时间获取失败");
                }
                urgentPreList.add(urgent);
            }
        }else if(type.equals("Home")) {
            QueryWrapper<StuHomework> wrapperHome = new QueryWrapper<>();

            wrapperHome.eq("stu_id", student.getId());
            wrapperHome.eq("finish", 0);
            wrapperHome.ge("gmt_deadline", new Date());
            wrapperHome.orderByAsc("gmt_deadline");
            List<StuHomework> stuHomeworks = stuHomeworkService.list(wrapperHome);
            for (StuHomework s : stuHomeworks) {
                Homework homework = homeworkService.getById(s.getHomeworkId());
                ToDoQuery urgent = new ToDoQuery();
                urgent.setBegin(sdf.format(homework.getGmtCreate()));
                urgent.setEnd(sdf.format(homework.getGmtDeadline()));
                urgent.setTitle(homework.getTitle());
                urgent.setType("Homework");
                try {
                    Date end = sdf.parse(urgent.getEnd());
                    Date now = sdf.parse(sdf.format(new Date()));
                    long days = (end.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
                    urgent.setRemaining(days);
                } catch (Exception e) {
                    throw new IntroException(20001, "时间获取失败");
                }
                urgentPreList.add(urgent);
            }
        }else if(type.equals("Time")) {
            QueryWrapper<StuTimedtest> wrapperTime = new QueryWrapper<>();

            wrapperTime.eq("stu_id", student.getId());
            wrapperTime.eq("finish", 0);
            wrapperTime.ge("gmt_deadline", new Date());
            wrapperTime.orderByAsc("gmt_deadline");
            List<StuTimedtest> stuTimedtests = stuTimedtestService.list(wrapperTime);
            for (StuTimedtest s : stuTimedtests) {
                Timedtest timedtest = timedtestService.getById(s.getTimedtestId());
                ToDoQuery urgent = new ToDoQuery();
                urgent.setBegin(sdf.format(timedtest.getGmtCreate()));
                urgent.setEnd(sdf.format(timedtest.getGmtDeadline()));
                urgent.setTitle(timedtest.getTitle());
                urgent.setType("Timedtest");
                try {
                    Date end = sdf.parse(urgent.getEnd());
                    Date now = sdf.parse(sdf.format(new Date()));
                    long days = (end.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
                    urgent.setRemaining(days);
                } catch (Exception e) {
                    throw new IntroException(20001, "时间获取失败");
                }
                urgentPreList.add(urgent);
            }
        }else{
            QueryWrapper<StuExperiment> wrapperExp = new QueryWrapper<>();

            wrapperExp.eq("stu_id", student.getId());
            wrapperExp.eq("finish", 0);
            wrapperExp.ge("gmt_deadline", new Date());
            wrapperExp.orderByAsc("gmt_deadline");
            List<StuExperiment> stuExperiments = stuExperimentService.list(wrapperExp);
            for (StuExperiment s : stuExperiments) {
                Experiment experiment = experimentService.getById(s.getExperimentId());
                ToDoQuery urgent = new ToDoQuery();
                urgent.setBegin(sdf.format(experiment.getGmtCreate()));
                urgent.setEnd(sdf.format(experiment.getGmtDeadline()));
                urgent.setTitle(experiment.getTitle());
                urgent.setType("Experiment");
                try {
                    Date end = sdf.parse(urgent.getEnd());
                    Date now = sdf.parse(sdf.format(new Date()));
                    long days = (end.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
                    urgent.setRemaining(days);
                } catch (Exception e) {
                    throw new IntroException(20001, "时间获取失败");
                }
                urgentPreList.add(urgent);
            }
        }
        return urgentPreList;
    }


    @Override
    public List<ToDoQuery> setMissedList(Student stu) {
        List<ToDoQuery> MissedList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //先将未完成的预习放到list
        QueryWrapper<StuPreview> wrapperPre = new QueryWrapper<>();
        wrapperPre.eq("stu_id", stu.getId());
        wrapperPre.eq("finish", 0);
        //截止日期在当前日期之前
        wrapperPre.le("gmt_deadline", new Date());
        List<StuPreview> stuPreviews = stuPreviewService.list(wrapperPre);
        for (StuPreview s : stuPreviews) {
            Preview preview = previewService.getById(s.getPreviewId());
            ToDoQuery toDo = new ToDoQuery();
            toDo.setBegin(sdf.format(preview.getGmtCreate()));
            toDo.setEnd(sdf.format(preview.getGmtDeadline()));
            toDo.setTitle(preview.getTitle());
            toDo.setType("Preview");
            MissedList.add(toDo);
        }

        //再将未完成的家庭作业放到list
        QueryWrapper<StuHomework> wrapperHome = new QueryWrapper<>();
        wrapperHome.eq("stu_id", stu.getId());
        wrapperHome.eq("finish", 0);
        wrapperHome.le("gmt_deadline", new Date());
        List<StuHomework> stuHomeworks = stuHomeworkService.list(wrapperHome);
        System.out.println("stuHomeworks:");
        System.out.println(stuHomeworks.toString());
        for (StuHomework s : stuHomeworks) {
            Homework homework = homeworkService.getById(s.getHomeworkId());
            ToDoQuery toDo = new ToDoQuery();
            toDo.setBegin(sdf.format(homework.getGmtCreate()));
            toDo.setEnd(sdf.format(homework.getGmtDeadline()));
            toDo.setTitle(homework.getTitle());
            toDo.setType("Homework");
            MissedList.add(toDo);
        }

        //再将未完成的timedTest放到list
        QueryWrapper<StuTimedtest> wrapperTime = new QueryWrapper<>();
        wrapperTime.eq("stu_id", stu.getId());
        wrapperTime.eq("finish", 0);
        wrapperTime.le("gmt_deadline", new Date());
        List<StuTimedtest> stuTimedtests = stuTimedtestService.list(wrapperTime);
        System.out.println("timedtest：");
        System.out.println(stuTimedtests.toString());
        for (StuTimedtest s : stuTimedtests) {
            Timedtest timedtest = timedtestService.getById(s.getTimedtestId());
            ToDoQuery toDo = new ToDoQuery();
            toDo.setBegin(sdf.format(timedtest.getGmtCreate()));
            toDo.setEnd(sdf.format(timedtest.getGmtDeadline()));
            toDo.setTitle(timedtest.getTitle());
            toDo.setType("Timedtest");
            MissedList.add(toDo);
        }
        //再将未完成的实验练习放到list
        QueryWrapper<StuExperiment> wrapperExp = new QueryWrapper<>();
        wrapperExp.eq("stu_id", stu.getId());
        wrapperExp.eq("finish", 0);
        wrapperExp.le("gmt_deadline", new Date());
        List<StuExperiment> stuExperiments = stuExperimentService.list(wrapperExp);
        System.out.println("stuExperiments：");
        System.out.println(stuExperiments.toString());
        for (StuExperiment s : stuExperiments) {
            Experiment experiment = experimentService.getById(s.getExperimentId());
            ToDoQuery toDo = new ToDoQuery();
            toDo.setBegin(sdf.format(experiment.getGmtCreate()));
            toDo.setEnd(sdf.format(experiment.getGmtDeadline()));
            toDo.setTitle(experiment.getTitle());
            toDo.setType("Experiment");
            MissedList.add(toDo);
        }
        //再将未完成的家庭作业放到list
        QueryWrapper<StuSummary> wrapperSum = new QueryWrapper<>();
        wrapperSum.eq("stu_id", stu.getId());
        wrapperSum.eq("finish", 0);
        wrapperSum.le("gmt_deadline", new Date());
        List<StuSummary> stuSummaries = stuSummaryService.list(wrapperSum);

        System.out.println("stuSummaries：");
        System.out.println(stuSummaries.toString());
        for (StuSummary s : stuSummaries) {
            Summary summary = summaryService.getById(s.getSummaryId());
            ToDoQuery toDo = new ToDoQuery();
            toDo.setBegin(sdf.format(summary.getGmtCreate()));
            toDo.setEnd(sdf.format(summary.getGmtDeadline()));
            toDo.setTitle(summary.getTitle());
            toDo.setType("Summary");
            MissedList.add(toDo);
        }

        return MissedList;
    }


}
