package com.introtoc.introService.controller.practice;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.introtoc.commonUtils.R;
import com.introtoc.introService.entity.*;
import com.introtoc.introService.entity.query.HomeworkQuery;
import com.introtoc.introService.service.*;
import com.introtoc.introService.utils.StringHandleUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author tengsss
 * @since 2021-04-12
 */


@Api(description = "课后作业管理")
@RestController
@RequestMapping("/introService/homework")
@CrossOrigin
public class HomeworkController {

    //注入关系
    @Autowired
    private HomeworkService homeworkService;
    //注入学生关系
    @Autowired
    private StuHomeworkService stuHomeworkService;
    //注入学生信息
    @Autowired
    private StudentService studentService;
    //注入题目服务类
    @Autowired
    private TopicService topicService;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private SectionService sectionService;

    /**
     * ==========================公共接口方法===========================
     */

    //1 查询某个学生的所有作业情况
    // 教师端【查看某个学生完成情况】
    // 学生端【我的课后作业】
    @ApiOperation(value = "学生端查询所有课后作业")
    @PostMapping("/findAllByStudent/{current}/{limit}/{stuId}")
    public R findHomeworkPageByStuId(@PathVariable long current,
                                     @PathVariable long limit,
                                     @PathVariable String stuId,
                                     @RequestBody(required = false) HomeworkQuery homeworkQuery) {

        //多条件组合
        System.out.println(homeworkQuery.toString());
        String title = homeworkQuery.getTitle();
        String chapterId = homeworkQuery.getChapter();
        String sectionId = homeworkQuery.getSection();
        String begin = homeworkQuery.getBegin();
        String end = homeworkQuery.getEnd();
        //这三个部分是preview里面的限制条件。不能用在stu_preview
        List<Homework> homeworkList;
        QueryWrapper<Homework> wrapperHomework = new QueryWrapper<>();
        //判断 加入构造条件
        if (!StringUtils.isEmpty(title)) {
            //判断标题不为空 加入条件
            wrapperHomework.like("title", title);
        }
        if (!StringUtils.isEmpty(chapterId)) {
            //判断标题不为空 加入条件
            wrapperHomework.eq("chapter_id", chapterId);
        }
        if (!StringUtils.isEmpty(sectionId)) {
            //判断标题不为空 加入条件
            wrapperHomework.eq("section_id", sectionId);
        }if (!StringUtils.isEmpty(begin)) {
            //构建条件
            wrapperHomework.ge("gmt_create", begin);
        }
        if (!StringUtils.isEmpty(end)) {
            //构建条件
            wrapperHomework.le("gmt_deadline", end);
        }
        homeworkList = homeworkService.list(wrapperHomework);//获取所有满足条件的preview
        //再找所有满足条件的stu_preview
        QueryWrapper<StuHomework> wrapper = new QueryWrapper<>();
        wrapper.eq("stu_id", stuId);
        List<StuHomework> allHomework = stuHomeworkService.list(wrapper); //找到该学生的所有preview
        List<StuHomework> stuHomeworkList = new ArrayList<>();
        //显示的标题集
        List<String> toShowNameList = new ArrayList<>();
        //找到所有满足章节和内容要求的stuPreview
        for (Homework h : homeworkList) {
            for (StuHomework sh : allHomework) {
                if (h.getId().equals(sh.getHomeworkId())) {
                    stuHomeworkList.add(sh);
                    toShowNameList.add(h.getTitle());
                }
            }
        }
        //创建Page对象
        Page<StuHomework> stuHomeworkPage = new Page<>(current, limit);
        stuHomeworkPage.setRecords(stuHomeworkList);
        stuHomeworkPage.setSize(limit);
        stuHomeworkPage.setTotal(stuHomeworkList.size());
        stuHomeworkPage.setCurrent(current);
        long total = stuHomeworkList.size();//总记录数
        List<StuHomework> records = stuHomeworkPage.getRecords();//数据list集合

        return R.ok().data("total", total).data("stuHomeworkPage", records).data("toShowName",toShowNameList);
    }

    //2 根据课后作业id查看详细课后作业信息包括题目
    // 学生端【查看完成练习】
    // 教师端【查看某个学生练习】
    @ApiOperation(value = "查询课后作业")
    @GetMapping("/findStuHomeworkById/{id}")
    public R findHomeworkById(@PathVariable String id) { //此id为stuHomeworkId 查看详情用
        StuHomework stuHomeworkInfo = stuHomeworkService.getInfoById(id);
        //将题目列表取出来封装到list中
        List<Topic> topicList = new ArrayList<>();
        String topics = stuHomeworkInfo.getTopics();
        String[] topicsArr = topics.split(" ");
        for (String topicId : topicsArr) {
            topicList.add(topicService.getById(topicId));
        }
        //将学生的答案集封装到list中
        List<String> answerList = new ArrayList<>();
        String answer = stuHomeworkInfo.getStuAnswers();
        if (answer != null && stuHomeworkInfo.getFinish()) {
            String[] ansArr = answer.split("_ans_"); //在回答中可能会出现空格的情况，我们使用_ans_来隔开每一个答案
            answerList.addAll(Arrays.asList(ansArr));
        }

        return R.ok().data("stuHomeworkInfo", stuHomeworkInfo).data("topicList", topicList).data("answerList", answerList);
    }

    /**
     * ==========================教师端接口方法===========================
     */

    //3 查询已发布所有课后作业
    // 教师端【课后作业管理】 封装好查询实体类
    @ApiOperation(value = "教师端查询所有课后作业")
    @GetMapping("/findHomeworkPage/{current}/{limit}")
    public R findHomeworkPage(@PathVariable long current,
                              @PathVariable long limit) {
        //创建Page对象
        Page<Homework> homeworkPage = new Page<>(current, limit);
        //构建条件
        QueryWrapper<Homework> wrapper = new QueryWrapper<>();

        //  排序
        wrapper.orderByDesc("gmt_create");

        //调用方法实现查询分页
        homeworkService.page(homeworkPage, wrapper);

        long total = homeworkPage.getTotal();//总记录数
        List<Homework> records = homeworkPage.getRecords();//数据list集合
        //将完成情况赋值给数组
        List<String> toShowCompletion = new ArrayList<>();
        List<String> chapterNameList = new ArrayList<>();
        List<String> sectionNameList = new ArrayList<>();
        if (records != null) {
            for (Homework h : records) {
                String chapterName = chapterService.getById(h.getChapterId()).getTitle();
                chapterNameList.add(chapterName);
                sectionNameList.add(sectionService.getById(h.getSectionId()).getTitle());
                //找到每个preview下所有完成学生情况
                int finish = stuHomeworkService.findFinishCount(h);
                int all = stuHomeworkService.findAllCount(h);
                toShowCompletion.add(finish+"/"+all);
            }
        }
        return R.ok().data("total", total).data("homeworkList", records).data("completion",toShowCompletion)
                .data("chapterNameList",chapterNameList).data("sectionNameList",sectionNameList);
    }

    //4 查询将截至事件
    // 教师端【紧急任务】按截止时间查询未完成
    @ApiOperation(value = "教师端查询紧急任务")
    @GetMapping("/findDeadline/{current}/{limit}")
    public R findDeadlineHomework(@PathVariable long current,
                                  @PathVariable long limit) {
        //按截止时间进行倒叙，要求完成状态为未完成
        Page<StuHomework> stuHomeworkPage = new Page<>(current, limit);
        //构造条件
        QueryWrapper<StuHomework> wrapper = new QueryWrapper<>();
        wrapper.eq("finish", 0);
        //  排序
        wrapper.orderByDesc("gmt_deadline");
        stuHomeworkService.page(stuHomeworkPage, wrapper);
        long total = stuHomeworkPage.getTotal();//总记录数
        List<StuHomework> records = stuHomeworkPage.getRecords();//数据list集合
        return R.ok().data("total", total).data("stuHomeworkPage", records);
    }

    //5 查询某次 homework下的所有stuHomework
    // 教师端【查询某次所有学生课后作业】
    @ApiOperation(value = "查询某次课后作业下所有练习信息")
    @GetMapping("/findStuHomework/{current}/{limit}/{id}")
    public R findStuHomeworkByHomeworkId(@PathVariable long current,
                                         @PathVariable long limit,
                                         @PathVariable String id) {

        Homework homework = homeworkService.getById(id);
        int finish = stuHomeworkService.findFinishCount(homework);
        int all = stuHomeworkService.findAllCount(homework);
        String completion = finish+"/"+all;

        String toShowName = homework.getTitle();

        Page<StuHomework> homeworkPage = new Page<>(current, limit);
        //构造条件
        QueryWrapper<StuHomework> wrapper = new QueryWrapper<>();
        wrapper.eq("homework_id", id);
        //  排序
        wrapper.orderByDesc("finish");
        stuHomeworkService.page(homeworkPage, wrapper);
        long total = homeworkPage.getTotal();//总记录数
        List<StuHomework> records = homeworkPage.getRecords();//数据list集合
        //显示的姓名集
        List<String> stuNameList = new ArrayList<>();
        for(StuHomework stuHomework: records){
            System.out.println("==================");
            System.out.println(studentService.getStuInfoById(stuHomework.getStuId()).getName());
            stuNameList.add(studentService.getStuInfoById(stuHomework.getStuId()).getName());
        }

        return R.ok().data("total", total).data("stuHomeworkPageById", records).data("completion", completion).data("toShowName",toShowName).data("stuNameList",stuNameList);
    }

    //6 修改课后作业信息
    // 教师端【修改课后作业】
    //6.1 现根据id查询某个课后作业进行回显
    @ApiOperation(value = "教师端修改课后作业——查询到该作业内容")
    @GetMapping("getHomework/{id}")
    public R getHomeworkById(@PathVariable String id) {
        System.out.println(id+"++++++"+homeworkService.getById(id));
        return R.ok().data("homeworkInfo", homeworkService.getById(id));
    }

    //6.2 之后保存课程信息
    @ApiOperation(value = "教师端修改课后作业-课后作业更新")
    @PostMapping("/updateHomework")
    public R updateHomework(@RequestBody Homework homework) {
        homework.setGmtModified(new Date()); //设置更新时间
        boolean flag = homeworkService.updateById(homework);
        return flag ? R.ok() : R.error();
    }

    //7 删除课前预习
    // 教师端【题目删除】
    @ApiOperation(value = "逻辑删除课后作业")
    @DeleteMapping("/deleteHomeworkById/{id}")
    public R deleteStuById(
            @ApiParam(name = "id", value = "课后作业ID", required = true)
            @PathVariable String id) {
        //删除课前预习，应该删除当前stuHomework中的所有该homework数据
        QueryWrapper<StuHomework> stuPreviewQueryWrapper = new QueryWrapper<>();
        stuPreviewQueryWrapper.eq("homework_id",id);
        stuHomeworkService.remove(stuPreviewQueryWrapper);
        boolean flag = homeworkService.removeById(id);
        System.out.println(flag ? "删除成功" : "删除失败");
        return flag ? R.ok() : R.error();
    }

    //8 教师端添加课后作业 完成homework的基本信息填写后，自动发放给每位同学到stuHomework表中
    // 教师端【添加课后作业】
    @ApiOperation(value = "教师端添加课后作业")
    @PostMapping("/addHomework")
    public R addHomework(@RequestBody Homework homework) {

        //先把该基本信息保存到Homework表
        homework.setGmtCreate(new Date());
        homework.setGmtModified(new Date());
        homeworkService.save(homework);

        int count = homework.getCount(); //题目数量
        String chapterId = homework.getChapterId(); //章节id
        String sectionId = homework.getSectionId(); //小节ID
        List<String> stuIdList = studentService.findStuIdList(); //找到所有学生的id集合
        List<Topic> topicList = topicService.findAllTopicByChapterAndSection(chapterId, sectionId); //找到要求章节下的题目集合

        for (String stuId : stuIdList) {
            //为每个学生添加任务 放到stu_preview
            StuHomework stuHomework = new StuHomework();
            stuHomework.setStuId(stuId);
            stuHomework.setHomeworkId(homework.getId());
            List<String> topicIds = new ArrayList<>();
            //根据题目集生产随机一组
            StringBuilder topics = new StringBuilder();
            for (int i = 0; i < count; i++) {
                int index = (int) (Math.random() * topicList.size());
                String tempId = topicList.get(index).getId();
                if (topicIds.contains(tempId)) {
                    i--;
                } else {
                    topicIds.add(tempId);
                }
            }
            for (String topicId : topicIds) {
                topics.append(topicId).append(" ");
            }
            stuHomework.setTopics(topics.toString());
            stuHomework.setGmtFinish(null);
            stuHomework.setGmtDeadline(homework.getGmtDeadline());
            stuHomeworkService.save(stuHomework);
        }
        return R.ok();
    }

    /**
     * ==========================学生端接口方法===========================
     */

    //9 学生完成练习提交 学生端【提交作业】 相当于修改stuHomework的信息
    @ApiOperation(value = "学生端添加完成后的课后作业")
    @PostMapping("/updateStuHomework")
    public R updateStuHomework(@RequestBody StuHomework stuHomework) {
        //需要前端将答案拼接处理好封装到stuHomework中传过来
        //如果前端不方便拼接字符串，封装到一个list中后端处理 目前默认为前端封装成字符串形式过来（A_ans_B形式）
        StuHomework baseStuHomework = stuHomeworkService.getHomework(stuHomework);
        //将stuPreview换到base中去
        baseStuHomework.setStuAnswers(stuHomework.getStuAnswers());

        String[] topicIds = baseStuHomework.getTopics().split(" ");

        String[] ans = StringHandleUtils.splitAnswer(baseStuHomework.getStuAnswers(),topicIds.length);
        int score = StringHandleUtils.checkAnswers(topicIds,ans);

        baseStuHomework.setFinish(true);
        baseStuHomework.setScore(score);
        baseStuHomework.setGmtFinish(new Date());

        boolean save = stuHomeworkService.updateById(baseStuHomework);
        return save ? R.ok() : R.error();

    }

}

