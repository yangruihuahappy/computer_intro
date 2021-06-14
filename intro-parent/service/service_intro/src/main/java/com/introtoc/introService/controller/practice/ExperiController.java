package com.introtoc.introService.controller.practice;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.introtoc.commonUtils.R;
import com.introtoc.introService.entity.*;
import com.introtoc.introService.entity.query.ExperimentQuery;
import com.introtoc.introService.service.*;
import com.introtoc.serviceBase.exceptionHandler.IntroException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author tengsss
 * @since 2021-04-15
 */

@Api(description = "实验练习管理")
@RestController
@RequestMapping("/introService/experiment")
@CrossOrigin
public class ExperiController {

    //注入关系
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private StuExperimentService stuExperimentService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private SectionService sectionService;

    /**
     * ==========================公共接口方法===========================
     */

    //1 根据学生id查询所有实验练习情况
    //教师端 【查看某个学生实验情况】
    //学生端 【我的实验练习】
    @ApiOperation(value = "查询学生所有实验练习")
    @PostMapping("/findExperimentByStudent/{current}/{limit}/{stuId}")
    public R findExperimentByStudent(@PathVariable long current,
                                     @PathVariable long limit,
                                     @PathVariable String stuId,
                                     @RequestBody(required = false) ExperimentQuery experimentQuery) {

        //多条件组合
        System.out.println(experimentQuery.toString());
        String title = experimentQuery.getTitle();
        String chapterId = experimentQuery.getChapter();
        String sectionId = experimentQuery.getSection();
        String begin = experimentQuery.getBegin();
        String end = experimentQuery.getEnd();
        //这三个部分是preview里面的限制条件。不能用在stu_preview
        List<Experiment> experimentList;
        QueryWrapper<Experiment> wrapperExperiment = new QueryWrapper<>();
        //判断 加入构造条件
        if (!StringUtils.isEmpty(title)) {
            //判断标题不为空 加入条件
            wrapperExperiment.like("title", title);
        }
        if (!StringUtils.isEmpty(chapterId)) {
            //判断标题不为空 加入条件
            wrapperExperiment.eq("chapter_id", chapterId);
        }
        if (!StringUtils.isEmpty(sectionId)) {
            //判断标题不为空 加入条件
            wrapperExperiment.eq("section_id", sectionId);
        }if (!StringUtils.isEmpty(begin)) {
            //构建条件
            wrapperExperiment.ge("gmt_create", begin);
        }
        if (!StringUtils.isEmpty(end)) {
            //构建条件
            wrapperExperiment.le("gmt_deadline", end);
        }
        experimentList = experimentService.list(wrapperExperiment);//获取所有满足条件的preview
        //再找所有满足条件的stu_preview
        QueryWrapper<StuExperiment> wrapper = new QueryWrapper<>();
        wrapper.eq("stu_id", stuId);
        List<StuExperiment> allExperiment = stuExperimentService.list(wrapper); //找到该学生的所有preview
        List<StuExperiment> stuExperimentList = new ArrayList<>();
        //显示的标题集
        List<String> toShowNameList = new ArrayList<>();
        //找到所有满足章节和内容要求的stuPreview
        for (Experiment e : experimentList) {
            for (StuExperiment se : allExperiment) {
                if (e.getId().equals(se.getExperimentId())) {
                    stuExperimentList.add(se);
                    toShowNameList.add(e.getTitle());
                }
            }
        }
        //创建Page对象
        Page<StuExperiment> stuExperimentPage = new Page<>(current, limit);
        stuExperimentPage.setRecords(stuExperimentList);
        stuExperimentPage.setSize(limit);
        stuExperimentPage.setTotal(stuExperimentList.size());
        stuExperimentPage.setCurrent(current);
        long total = stuExperimentList.size();//总记录数
        List<StuExperiment> records = stuExperimentPage.getRecords();//数据list集合

        return R.ok().data("total", total).data("stuExperimentPage", records).data("toShowName",toShowNameList);
    }

    //2 根据stu_experiment id 查看具体实验练习信息
    //学生端 【查看实验练习】
    //教师端 【查看某个学生练习】【提交练习】用于完成练习
    @ApiOperation(value = "查询实验练习")
    @GetMapping("/findStuExperimentById/{id}")
    public R findHomeworkById(@PathVariable String id) { //此id为stuExperimentId 查看详情用
        StuExperiment stuExperimentInfo = stuExperimentService.getById(id);

        return R.ok().data("stuExperimentInfo", stuExperimentInfo);
    }

    /**
     * ==========================教师端接口方法===========================
     */

    //3 发布新的实验练习
    //教师端 【发布实验】
    @ApiOperation(value = "教师端添加实验")
    @PostMapping("/addExperiment")
    public R addExperiment(@RequestBody Experiment experiment) {

        experiment.setGmtCreate(new Date());
        experiment.setGmtModified(new Date());
        experimentService.save(experiment);
        //为每个学生添加相应的实验
        List<String> stuIdList = studentService.findStuIdList(); //找到所有学生的id集合
        for (String stuId : stuIdList) {
            StuExperiment stuExperiment = new StuExperiment();
            stuExperiment.setExperimentId(experiment.getId());
            stuExperiment.setStuId(stuId);
            stuExperiment.setGmtDeadline(experiment.getGmtDeadline());
            stuExperimentService.save(stuExperiment);
        }
        return R.ok();
    }

    //4 修改已发布练习
    //教师端 【修改实验】
    @ApiOperation(value = "修改实验——查询到实验进行回显")
    @GetMapping("/getExperimentById/{id}")
    public R getExperimentById(@PathVariable String id) {
        return R.ok().data("experimentInfo", experimentService.getById(id));
    }

    @ApiOperation(value = "修改实验——更新实验报告数据")
    @PostMapping("/updateExperiment")
    public R updateExperiment(@RequestBody Experiment experiment) {
        experiment.setGmtModified(new Date());
        System.out.println(experiment.toString());
        boolean flag = experimentService.updateById(experiment);

        return flag ? R.ok() : R.error().message("更新失败");
    }

    //5 删除实验练习
    //教师端 【删除实验】
    @ApiOperation(value = "逻辑删除实验报告")
    @DeleteMapping("/deleteExperimentById/{id}")
    public R deleteExperimentById(
            @ApiParam(name = "id", value = "报告ID", required = true)
            @PathVariable String id) {
        //删除课前预习，应该删除当前stuTimedtest中的所有该timedtest数据
        QueryWrapper<StuExperiment> stuExperimentQueryWrapper = new QueryWrapper<>();
        stuExperimentQueryWrapper.eq("experiment_id",id);
        stuExperimentService.remove(stuExperimentQueryWrapper);
        //这里需要将实验练习下所有同学的实验信息都删除
        boolean flag = experimentService.removeExperiment(id);
        return flag ? R.ok() : R.error().message("删除失败");
    }

    //6 查询所有已发布的实验练习
    //教师端 【实验练习管理】封装实体查询类
    @ApiOperation(value = "教师端查询所有实验练习")
    @GetMapping("/findExperimentPage/{current}/{limit}")
    public R findExperimentPage(@PathVariable long current,
                                @PathVariable long limit) {
        //创建Page对象
        Page<Experiment> experimentPage = new Page<>(current, limit);
        //构建条件
        QueryWrapper<Experiment> wrapper = new QueryWrapper<>();

        //  排序
        wrapper.orderByDesc("gmt_create");

        //调用方法实现查询分页
        experimentService.page(experimentPage, wrapper);

        long total = experimentPage.getTotal();//总记录数
        List<Experiment> records = experimentPage.getRecords();//数据list集合

        //将完成情况赋值给数组
        List<String> toShowCompletion = new ArrayList<>();
        List<String> chapterNameList = new ArrayList<>();
        List<String> sectionNameList = new ArrayList<>();
        if (records != null) {
            for (Experiment e : records) {

                String chapterName = chapterService.getById(e.getChapterId()).getTitle();
                chapterNameList.add(chapterName);
                sectionNameList.add(sectionService.getById(e.getSectionId()).getTitle());
                //找到每个preview下所有完成学生情况
                int finish = stuExperimentService.findFinishCount(e);
                int all = stuExperimentService.findAllCount(e);
                toShowCompletion.add(finish+"/"+all);
            }
        }
        System.out.println("==========显示返回的实验练习===========");
        System.out.println(records.toString());
        return R.ok().data("total", total).data("experimentList", records).data("completion",toShowCompletion)
                .data("chapterNameList",chapterNameList).data("sectionNameList",sectionNameList);
    }

    //7 查询即将截止事件
    //教师端 【将截至任务】
    @ApiOperation(value = "教师端查询将截至练习")
    @PostMapping("/findDeadline/{current}/{limit}")
    public R findDeadlineExperiment(@PathVariable long current,
                                    @PathVariable long limit) {
        //按截止时间进行倒叙，要求完成状态为未完成
        Page<StuExperiment> stuExperimentPage = new Page<>(current, limit);
        //构造条件
        QueryWrapper<StuExperiment> wrapper = new QueryWrapper<>();
        wrapper.eq("finish", 0);
        //  排序
        wrapper.orderByDesc("gmt_deadline");
        stuExperimentService.page(stuExperimentPage, wrapper);
        long total = stuExperimentPage.getTotal();//总记录数
        List<StuExperiment> records = stuExperimentPage.getRecords();//数据list集合
        return R.ok().data("total", total).data("stuExperimentPageDeadline", records);
    }

    //8 查询某次实验下的所有学生实验情况
    //教师端 【实验的学生完成详情】
    @ApiOperation(value = "查询某次实验下所有练习信息")
    @GetMapping("/findStuExperiment/{current}/{limit}/{id}")
    public R findStuExperimentByExperimentId(@PathVariable long current,
                                             @PathVariable long limit,
                                             @PathVariable String id) {

        Experiment experiment = experimentService.getById(id);
        int finish = stuExperimentService.findFinishCount(experiment);
        int all = stuExperimentService.findAllCount(experiment);
        String completion = finish+"/"+all;

        String toShowName = experiment.getTitle();

        Page<StuExperiment> stuExperimentPage = new Page<>(current, limit);
        //构造条件
        QueryWrapper<StuExperiment> wrapper = new QueryWrapper<>();
        wrapper.eq("experiment_id", id);
        //  排序
        wrapper.orderByDesc("finish");
        stuExperimentService.page(stuExperimentPage, wrapper);
        long total = stuExperimentPage.getTotal();//总记录数
        List<StuExperiment> records = stuExperimentPage.getRecords();//数据list集合
        //显示的姓名集
        List<String> stuNameList = new ArrayList<>();
        for(StuExperiment stuExperiment: records){
            System.out.println(studentService.getStuInfoById(stuExperiment.getStuId()).getName());
            stuNameList.add(studentService.getStuInfoById(stuExperiment.getStuId()).getName());
        }
        return R.ok().data("total", total).data("stuExperimentPageById", records).data("completion", completion).data("toShowName",toShowName).data("stuNameList",stuNameList);
    }

    //9 根据stuExperiment_id查询学生练习信息进行批改
    //教师端 【批改学生实验】
    @ApiOperation(value = "教师批改实验练习")
    @PostMapping("/updateStuExperimentByTeacher")
    public R updateStuExperimentByTeacher(@RequestBody StuExperiment stuExperiment) {
        StuExperiment baseStuExperiment = stuExperimentService.getById(stuExperiment);
        if (baseStuExperiment.getFinish()) { //判断该练习是否完成
            BeanUtils.copyProperties(stuExperiment, baseStuExperiment);
            baseStuExperiment.setScore(stuExperiment.getScore());
            stuExperimentService.updateById(baseStuExperiment);
            return R.ok();
        } else {
            throw new IntroException(20001, "学生还未提交报告，不可评分");
        }
    }

    //10 查询目前还未批改的实验报告
    //教师端 【查询未批改实验】
    @ApiOperation(value = "查询未批改的所有练习信息")
    @GetMapping("/findNoScoreExperiment/{current}/{limit}")
    public R findNoScoreExperiment(@PathVariable long current,
                                   @PathVariable long limit) {
        Page<StuExperiment> stuExperimentPage = new Page<>(current, limit);
        //构造条件
        QueryWrapper<StuExperiment> wrapper = new QueryWrapper<>();
        wrapper.eq("score", -1);
        wrapper.orderByDesc("gmt_finish");
        stuExperimentService.page(stuExperimentPage, wrapper);
        long total = stuExperimentPage.getTotal();//总记录数
        List<StuExperiment> records = stuExperimentPage.getRecords();//数据list集合
        return R.ok().data("total", total).data("stuExperimentNoScore", records);
    }

    /**
     * ==========================学生端接口方法===========================
     */

    //11 学生提交实验练习
    @ApiOperation(value = "学生端提交完成后的课后作业")
    @PostMapping("/updateStuExperiment")
    public R updateStuExperiment(@RequestBody StuExperiment stuExperiment) {
        //此时传过来的stuExperiment应该是已经在前端调用过oss服务且将文件上传到云端的，并将得到的云端路径和id封装到stuExperiment中
        StuExperiment baseStuExperiment = stuExperimentService.getById(stuExperiment.getId());
        baseStuExperiment.setOssUrl(stuExperiment.getOssUrl());
        baseStuExperiment.setFinish(true); //设置完成状态
        baseStuExperiment.setGmtFinish(new Date());
        boolean flag = stuExperimentService.updateById(baseStuExperiment);
        return flag ? R.ok() : R.error().message("提交失败");
    }

    //12 通过id获得下载地址
    @ApiOperation(value = "通过id获取下载地址")
    @GetMapping("/getBaseFileUrl/{experimentId}")
    public R getBaseFileUrl(@PathVariable String experimentId){
        System.out.println(experimentId);
        Experiment experiment = experimentService.getById(experimentId);
        if(experiment.getOriginalUrl()==null){
            throw new IntroException(20001,"当前实验没有模板");
        }
        return R.ok().data("fileUrl",experiment.getOriginalUrl());
    }
}

