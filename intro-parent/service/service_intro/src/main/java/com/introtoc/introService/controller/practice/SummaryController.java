package com.introtoc.introService.controller.practice;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.introtoc.commonUtils.R;
import com.introtoc.introService.entity.*;
import com.introtoc.introService.entity.query.ExperimentQuery;
import com.introtoc.introService.entity.query.SummaryQuery;
import com.introtoc.introService.service.StuSummaryService;
import com.introtoc.introService.service.StudentService;
import com.introtoc.introService.service.SummaryService;
import com.introtoc.serviceBase.exceptionHandler.IntroException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author tengsss
 * @since 2021-04-16
 */
@Api(description = "每周总结管理")
@RestController
@RequestMapping("/introService/summary")
@CrossOrigin
public class SummaryController {

    //注入关系
    @Autowired
    private SummaryService summaryService;
    @Autowired
    private StuSummaryService stuSummaryService;
    //注入学生信息
    @Autowired
    private StudentService studentService;

    //本学期开始日期
    private final String startDate = "2021-03-01";

    /**
     * ==========================公共接口方法===========================
     */
    //1 根据学生id查询某个学生的所有每周总结
    //教师端【查看某个学生每周总结】
    //学生端【我的每周总结】
    @ApiOperation(value = "查询学生所有每周总结")
    @PostMapping("/findAllSummaryByStu/{current}/{limit}/{stuId}")
    public R findAllSummaryByStu(@PathVariable long current,
                                 @PathVariable long limit,
                                 @PathVariable String stuId,
                                 @RequestBody(required = false) SummaryQuery summaryQuery) {

        //多条件组合
        System.out.println(summaryQuery.toString());
        String title = summaryQuery.getTitle();
        String begin = summaryQuery.getBegin();
        String end = summaryQuery.getEnd();
        //这三个部分是preview里面的限制条件。不能用在stu_preview
        List<Summary> summaryList;
        QueryWrapper<Summary> wrapperSummary = new QueryWrapper<>();
        //判断 加入构造条件
        if (!StringUtils.isEmpty(title)) {
            //判断标题不为空 加入条件
            wrapperSummary.like("title", title);
        }
        if (!StringUtils.isEmpty(begin)) {
            //构建条件
            wrapperSummary.ge("gmt_create", begin);
        }
        if (!StringUtils.isEmpty(end)) {
            //构建条件
            wrapperSummary.le("gmt_deadline", end);
        }
        summaryList = summaryService.list(wrapperSummary);//获取所有满足条件的summary
        //再找所有满足条件的stu_preview
        QueryWrapper<StuSummary> wrapper = new QueryWrapper<>();
        wrapper.eq("stu_id", stuId);
        List<StuSummary> allSummary = stuSummaryService.list(wrapper); //找到该学生的所有preview
        List<StuSummary> stuSummaryList = new ArrayList<>();
        //显示的标题集
        List<String> toShowNameList = new ArrayList<>();
        //找到所有满足章节和内容要求的stuPreview
        for (Summary s : summaryList) {
            for (StuSummary ss : allSummary) {
                if (s.getId().equals(ss.getSummaryId())) {
                    stuSummaryList.add(ss);
                    toShowNameList.add(s.getTitle());
                }
            }
        }
        //创建Page对象
        Page<StuSummary> stuSummaryPage = new Page<>(current, limit);
        stuSummaryPage.setRecords(stuSummaryList);
        stuSummaryPage.setSize(limit);
        stuSummaryPage.setTotal(stuSummaryList.size());
        stuSummaryPage.setCurrent(current);
        long total = stuSummaryList.size();//总记录数
        List<StuSummary> records = stuSummaryPage.getRecords();//数据list集合

        return R.ok().data("total", total).data("stuSummaryPage", records).data("toShowName", toShowNameList);
    }

    //2 根据stu_Summary_id查看某位同学具体的每周总结
    //教师端【每周总结评分】
    //学生端【我的每周总结】
    @ApiOperation(value = "查看具体每周总结")
    @GetMapping("/findStuSummaryById/{id}")
    public R findStuSummaryById(@PathVariable String id) {
        StuSummary stuSummaryInfo = stuSummaryService.getById(id);
        return R.ok().data("stuSummaryInfo", stuSummaryInfo);
    }

    // 计算当前时间所在周次是否发布过每周总结
    @ApiOperation(value = "计算当前时间是否发布过总结")
    @GetMapping("/judgePublish")
    public R judgePublish() {
        boolean flag;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = df.parse(startDate);
            Date end = new Date();
            long diff = end.getTime() - start.getTime();
            long diffWeeks = diff / (7 * 24 * 60 * 60 * 1000);
            String description = "第" + (diffWeeks + 1) + "周的每周总结";
            QueryWrapper<Summary> wrapper = new QueryWrapper<>();
            wrapper.eq("description", description);
            Summary summary = summaryService.getOne(wrapper);
            //查询所有summary
            List<Summary> summaryList = summaryService.list(null);
            flag = summaryList.contains(summary);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IntroException(20001, "设置日期出现异常");
        }
        System.out.println(flag);

        return R.ok().data("flag", flag);
    }

    /**
     * ==========================教师端接口方法===========================
     */
    //3 发布新的每周总结
    //教师端【发布任务】
    @ApiOperation(value = "发布每周总结")
    @PostMapping("/addSummary")
    public R addSummary(@RequestBody Summary summary) {
        Summary baseSummary = new Summary();
        BeanUtils.copyProperties(summary, baseSummary);

        //先进行日期自动加七天设置deadline
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance(); //创建日历对象
        cal.setTime(summary.getGmtCreate()); //设置初始创建时间
        cal.add(Calendar.DAY_OF_WEEK, +7);
        baseSummary.setGmtDeadline(cal.getTime());

        //设置描述 自动计算第几周的每周总结
        try {
            Date start = df.parse(startDate);
            Date end = baseSummary.getGmtCreate();
            long diff = end.getTime() - start.getTime();
            long diffWeeks = diff / (7 * 24 * 60 * 60 * 1000);
            String description = "第" + (diffWeeks + 1) + "周的每周总结";
            baseSummary.setDescription(description);
        } catch (Exception e) {
            throw new IntroException(20001, "设置日期出现异常");
        }


        summaryService.save(baseSummary);
        //为每个学生添加
        List<String> stuIdList = studentService.findStuIdList(); //找到所有学生的id集合
        for (String stuId : stuIdList) {
            StuSummary stuSummary = new StuSummary();
            stuSummary.setSummaryId(baseSummary.getId());
            stuSummary.setStuId(stuId);
            stuSummary.setGmtDeadline(baseSummary.getGmtDeadline());
            stuSummaryService.save(stuSummary);
        }
        return R.ok();
    }

    //3.1 为某个同学发布单独的每周总结
    //教师端【单独添加某个同学的每周总结】
    @ApiOperation(value = "为某个学生单独发布每周总结")
    @PostMapping("/addSummaryForStu/{stuId}/{summaryId}")
    public R addSummaryForStu(@PathVariable String stuId,
                              @PathVariable String summaryId) {
        //为某个学生添加
        Summary summary = summaryService.getById(summaryId);
        StuSummary stuSummary = new StuSummary();
        stuSummary.setSummaryId(summaryId);
        stuSummary.setStuId(stuId);
        stuSummary.setGmtDeadline(summary.getGmtDeadline());
        stuSummaryService.save(stuSummary);
        return R.ok();
    }


    //4 修改已发布的每周总结
    //教师端【修改每周总结】
    @ApiOperation(value = "修改总结——查询到总结进行回显")
    @GetMapping("/getSummaryById/{id}")
    public R getExperimentById(@PathVariable String id) {
        return R.ok().data("SummaryInfo", summaryService.getById(id));
    }

    @ApiOperation(value = "修改总结——更新每周总结数据")
    @PostMapping("/updateSummary")
    public R updateSummary(@RequestBody Summary summary) {
        summary.setGmtModified(new Date());
        boolean flag = summaryService.updateById(summary);
        return flag ? R.ok() : R.error().message("更新失败");
    }

    //5 删除每周总结
    //教师端【删除每周总结】
    @ApiOperation(value = "删除教师已发布的每周总结")
    @DeleteMapping("/deleteSummaryById/{id}")
    public R deleteSummaryById(@PathVariable String id) {

        //需要先将stuSummary里面的数据删除
        QueryWrapper<StuSummary> wrapper = new QueryWrapper<>();
        wrapper.eq("summary_id", id);
        boolean remove = stuSummaryService.remove(wrapper);
        //再将改题目删除
        boolean flag = summaryService.deleteSummary(id);
        return remove && flag ? R.ok() : R.error().message("删除失败");
    }

    //6 查询所有已发布每周总结
    //教师端【每周总结管理】
    @ApiOperation(value = "查询所有每周总结")
    @PostMapping("/findSummaryPage/{current}/{limit}")
    public R findSummaryPage(@PathVariable long current,
                             @PathVariable long limit,
                             @RequestBody(required = false) SummaryQuery summaryQuery) {
        //创建Page对象
        Page<Summary> summaryPage = new Page<>(current, limit);
        //构建条件
        QueryWrapper<Summary> wrapper = new QueryWrapper<>();
        String begin = summaryQuery.getBegin();
        String end = summaryQuery.getEnd();
        String title = summaryQuery.getTitle();
        if (!StringUtils.isEmpty(begin)) {
            //构建条件
            wrapper.ge("gmt_create", begin);
        }
        if (!StringUtils.isEmpty(end)) {
            //构建条件
            wrapper.le("gmt_deadline", end);
        }
        if (!StringUtils.isEmpty(title)) {
            //判断标题不为空 加入条件
            wrapper.like("title", title);
        }
        wrapper.orderByDesc("gmt_create");
        summaryService.page(summaryPage, wrapper);
        long total = summaryPage.getTotal();//总记录数
        List<Summary> records = summaryPage.getRecords();//数据list集合

        //将完成情况赋值给数组
        List<String> toShowCompletion = new ArrayList<>();
        if (records != null) {
            for (Summary s : records) {
                //找到每个Summary下所有完成学生情况
                int finish = stuSummaryService.findFinishCount(s);
                int all = stuSummaryService.findAllCount(s);
                toShowCompletion.add(finish + "/" + all);
            }
        }

        return R.ok().data("total", total).data("summaryPage", records).data("completion", toShowCompletion);
    }


    //7 查询所有截止日期还未完成事件
    //教师端【待提醒任务】
    @ApiOperation(value = "教师端查询将截至总结")
    @PostMapping("/findDeadline/{current}/{limit}")
    public R findDeadlineExperiment(@PathVariable long current,
                                    @PathVariable long limit) {
        //按截止时间进行倒叙，要求完成状态为未完成
        Page<StuSummary> stuSummaryPage = new Page<>(current, limit);
        //构造条件
        QueryWrapper<StuSummary> wrapper = new QueryWrapper<>();
        wrapper.eq("finish", 0);
        //  排序
        wrapper.orderByDesc("gmt_deadline");
        stuSummaryService.page(stuSummaryPage, wrapper);
        long total = stuSummaryPage.getTotal();//总记录数
        List<StuSummary> records = stuSummaryPage.getRecords();//数据list集合
        return R.ok().data("total", total).data("stuSummaryPageDeadline", records);
    }

    //8 根据每周总结id查询stuSummary里的完成情况
    //教师端【学生的完成情况】
    @ApiOperation(value = "查询某次总结下所有总结信息")
    @GetMapping("/findStuSummaryBySummaryId/{current}/{limit}/{id}")
    public R findStuSummaryBySummaryId(@PathVariable long current,
                                       @PathVariable long limit,
                                       @PathVariable String id) {

        Summary summary = summaryService.getById(id);
        int finish = stuSummaryService.findFinishCount(summary);
        int all = stuSummaryService.findAllCount(summary);
        String completion = finish + "/" + all;
        String toShowName = summary.getTitle();
        String description = summary.getDescription();

        Page<StuSummary> stuSummaryPage = new Page<>(current, limit);
        //构造条件
        QueryWrapper<StuSummary> wrapper = new QueryWrapper<>();
        wrapper.eq("summary_id", id);
        //  排序
        wrapper.orderByDesc("finish");
        stuSummaryService.page(stuSummaryPage, wrapper);
        long total = stuSummaryPage.getTotal();//总记录数
        List<StuSummary> records = stuSummaryPage.getRecords();//数据list集合
        //显示的姓名集
        List<String> stuNameList = new ArrayList<>();
        for (StuSummary stuSummary : records) {
            System.out.println("==================");
            System.out.println(studentService.getStuInfoById(stuSummary.getStuId()).getName());
            stuNameList.add(studentService.getStuInfoById(stuSummary.getStuId()).getName());
        }
        return R.ok().data("total", total).data("stuSummaryPageById", records).data("completion", completion)
                .data("toShowName", toShowName).data("stuNameList", stuNameList).data("description", description);
    }


    //9 根据stuSummaryId对每周总结进行评分
    //教师端【每周总结评分】
    @ApiOperation(value = "教师对总结进行评分")
    @PostMapping("/updateStuSummaryByTeacher")
    public R updateStuSummaryByTeacher(@RequestBody StuSummary stuSummary) {
        StuSummary baseStuSummary = stuSummaryService.getById(stuSummary);
        if (baseStuSummary.getFinish()) { //判断该练习是否完成
            BeanUtils.copyProperties(stuSummary, baseStuSummary);
            baseStuSummary.setScore(stuSummary.getScore());
            stuSummaryService.updateById(baseStuSummary);
            return R.ok();
        } else {
            throw new IntroException(20001, "学生还未提交报告，不可评分");
        }
    }

    //10 查询待评分的总结
    //教师端【未评分总结】
    @ApiOperation(value = "查询未评分的总结信息")
    @GetMapping("/findNoScoreSummary/{current}/{limit}")
    public R findNoScoreSummary(@PathVariable long current,
                                @PathVariable long limit) {
        Page<StuSummary> stuSummaryPage = new Page<>(current, limit);
        //构造条件
        QueryWrapper<StuSummary> wrapper = new QueryWrapper<>();
        wrapper.eq("score", -1);
        wrapper.orderByDesc("gmt_finish");
        stuSummaryService.page(stuSummaryPage, wrapper);
        long total = stuSummaryPage.getTotal();//总记录数
        List<StuSummary> records = stuSummaryPage.getRecords();//数据list集合
        return R.ok().data("total", total).data("stuSummaryNoScore", records);
    }

    /**
     * ==========================学生端接口方法===========================
     */
    //11 学生提交总结
    @ApiOperation(value = "学生端提交完成后的总结")
    @PostMapping("/updateStuSummary")
    public R updateStuSummary(@RequestBody StuSummary stuSummary) {
        System.out.println(stuSummary.toString());
        //通过学生id和summaryId查找stuSummary
        QueryWrapper<StuSummary> wrapper = new QueryWrapper<>();
        wrapper.eq("stu_id", stuSummary.getStuId());
        wrapper.eq("summary_id", stuSummary.getSummaryId());
        StuSummary baseStuSummary = stuSummaryService.getOne(wrapper);

        if (baseStuSummary.getScore() == -1 && stuSummary.getScore()!=null) {  //教师端批改
            baseStuSummary.setScore(stuSummary.getScore());
        }
        System.out.println("查询到的base：" + baseStuSummary.toString());
        System.out.println(stuSummary.getContent());
        baseStuSummary.setContent(stuSummary.getContent());
        baseStuSummary.setFinish(true); //设置完成状态
        baseStuSummary.setGmtFinish(new Date());
        System.out.println("==============================");
        System.out.println(baseStuSummary.toString());
        boolean flag = stuSummaryService.updateById(baseStuSummary);
        return flag ? R.ok() : R.error().message("提交失败");
    }

}

