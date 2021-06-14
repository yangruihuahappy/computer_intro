package com.introtoc.introService.controller.practice;


import com.introtoc.introService.entity.StuPreview;
import com.introtoc.introService.entity.Topic;
import com.introtoc.introService.service.*;
import com.introtoc.introService.utils.StringHandleUtils;
import com.introtoc.serviceBase.exceptionHandler.IntroException;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.introtoc.commonUtils.R;
import com.introtoc.introService.entity.Preview;
import com.introtoc.introService.entity.query.PreviewQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author tengsss
 * @theme 课前预习管理
 * @since 2021-04-08
 */
@Api(description = "课前预习管理")
@RestController
@RequestMapping("/introService/preview")
@CrossOrigin
public class PreviewController {

    //注入
    @Autowired
    private PreviewService previewService;
    //注入学生和预习关系
    @Autowired
    private StuPreviewService stuPreviewService;
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

    public PreviewController(StuPreviewService stuPreviewService, StudentService studentService, TopicService topicService) {
        this.stuPreviewService = stuPreviewService;
        this.studentService = studentService;
        this.topicService = topicService;
    }


    /**
     * ==========================公共接口方法===========================
     */

    //1 根据学生id进行查询所有课前预习状态
    // 学生端【我的练习】-【课前预习】
    // 教师端【查看学生进度】封装学生查询实体（是否完成、截止时间、学生id、内容、章节）
    @ApiOperation(value = "查询所有课前预习")
    @PostMapping("/findAllByStudent/{current}/{limit}/{stuId}")
    public R findAllByStudent(@PathVariable long current,
                              @PathVariable long limit,
                              @PathVariable String stuId,
                              @RequestBody(required = false) PreviewQuery previewQuery) {

        //多条件组合
        System.out.println(previewQuery.toString());
        String title = previewQuery.getTitle();
        String chapterId = previewQuery.getChapter();
        String sectionId = previewQuery.getSection();
        String begin = previewQuery.getBegin();
        String end = previewQuery.getEnd();
        //这三个部分是preview里面的限制条件。不能用在stu_preview
        List<Preview> previews;
        QueryWrapper<Preview> wrapperPreview = new QueryWrapper<>();
        //判断 加入构造条件
        if (!StringUtils.isEmpty(title)) {
            //判断标题不为空 加入条件
            wrapperPreview.like("title", title);
        }
        if (!StringUtils.isEmpty(chapterId)) {
            //判断标题不为空 加入条件
            wrapperPreview.eq("chapter_id", chapterId);
        }
        if (!StringUtils.isEmpty(sectionId)) {
            //判断标题不为空 加入条件
            wrapperPreview.eq("section_id", sectionId);
        }
        if (!StringUtils.isEmpty(begin)) {
            //构建条件
            wrapperPreview.ge("gmt_create", begin);
        }
        if (!StringUtils.isEmpty(end)) {
            //构建条件
            wrapperPreview.le("gmt_deadline", end);
        }
        previews = previewService.list(wrapperPreview);//获取所有满足条件的preview
        //再找所有满足条件的stu_preview
        List<StuPreview> allPreviews = stuPreviewService.listByStuId(stuId); //找到该学生的所有preview
        List<StuPreview> stuPreviewList = new ArrayList<>();
        //显示的标题集
        List<String> toShowNameList = new ArrayList<>();
        //找到所有满足章节和内容要求的stuPreview
        for (Preview p : previews) {
            for (StuPreview sp : allPreviews) {
                if (p.getId().equals(sp.getPreviewId())) {
                    stuPreviewList.add(sp);
                    toShowNameList.add(p.getTitle());
                }
            }
        }
        //创建Page对象
        Page<StuPreview> stuPreviewPage = new Page<>(current, limit);
        stuPreviewPage.setRecords(stuPreviewList);
        stuPreviewPage.setSize(limit);
        stuPreviewPage.setTotal(stuPreviewList.size());
        stuPreviewPage.setCurrent(current);
        long total = stuPreviewList.size();//总记录数
        List<StuPreview> records = stuPreviewPage.getRecords();//数据list集合

        return R.ok().data("total", total).data("stuPreviewPage", records).data("toShowName", toShowNameList);
    }

    // 2 根据id查询课前预习已完成所有内容，包含题目作答题目
    // 教师端【课前预习学生完成详情】
    // 学生端【查看已完成练习】
    @ApiOperation(value = "查询学生练习")
    @GetMapping("/findStuPreviewById/{id}")
    public R findStuPreviewById(@PathVariable String id) { //此id为stuPreviewId 查看详情用
        StuPreview stuPreviewInfo = stuPreviewService.getInfoById(id);
        //将题目列表取出来封装到list中
        List<Topic> topicList = new ArrayList<>();
        String topics = stuPreviewInfo.getTopics();
        String[] topicsArr = topics.split(" ");
        for (String topicId : topicsArr) {
            topicList.add(topicService.getById(topicId));
        }

        //将学生的答案集封装到list中
        List<String> answerList = new ArrayList<>();
        String answer = stuPreviewInfo.getStuAnswers();
        if (answer != null && stuPreviewInfo.getFinish()) {
            String[] ansArr = answer.split("_ans_"); //在回答中可能会出现空格的情况，我们使用_ans_来隔开每一个答案
            answerList.addAll(Arrays.asList(ansArr));
        }
        System.out.println(topicList.toString());

        return R.ok().data("stuPreviewInfo", stuPreviewInfo).data("topicList", topicList).data("answerList", answerList);
    }

    /**
     * ==========================教师端接口方法===========================
     */
    //3 查询教师发布的所有课前预习
    //教师端【课前预习管理】封装教师端查询实体（起止时间、内容、章节）
    @ApiOperation(value = "教师端查询所有课前预习")
    @GetMapping("/findAllByTeacher/{current}/{limit}")
    public R findAllByTeacher(@PathVariable long current,
                              @PathVariable long limit) {
        //创建Page对象
        Page<Preview> previewPage = new Page<>(current, limit);
        //构建条件
        QueryWrapper<Preview> wrapper = new QueryWrapper<>();

        //  排序
        wrapper.orderByDesc("gmt_create");

        //调用方法实现查询分页
        previewService.page(previewPage, wrapper);

        long total = previewPage.getTotal();//总记录数
        List<Preview> records = previewPage.getRecords();//数据list集合
        //将完成情况赋值给数组
        List<String> toShowCompletion = new ArrayList<>();
        List<String> chapterNameList = new ArrayList<>();
        List<String> sectionNameList = new ArrayList<>();
        if (records != null) {
            for (Preview p : records) {
                String chapterName = chapterService.getById(p.getChapterId()).getTitle();
                chapterNameList.add(chapterName);
                sectionNameList.add(sectionService.getById(p.getSectionId()).getTitle());
                //找到每个preview下所有完成学生情况
                int finish = stuPreviewService.findFinishCount(p);
                int all = stuPreviewService.findAllCount(p);
                toShowCompletion.add(finish+"/"+all);
            }
        }
        return R.ok().data("total", total).data("previewList", records).data("completion",toShowCompletion)
                .data("chapterNameList",chapterNameList).data("sectionNameList",sectionNameList);
    }

    //4 根据截止时间进行倒叙分页查询
    // 教师端【紧急任务】查询未完成、截止时间正序排序
    @ApiOperation(value = "教师端查询紧急任务")
    @GetMapping("/findDeadline/{current}/{limit}")
    public R findDeadlinePreview(@PathVariable long current,
                                 @PathVariable long limit) {
        //按截止时间进行正序，要求完成状态为未完成
        Page<StuPreview> previewPage = new Page<>(current, limit);
        //构造条件
        QueryWrapper<StuPreview> wrapper = new QueryWrapper<>();
        wrapper.eq("finish", 0);
        //  排序
        wrapper.orderByDesc("gmt_deadline");
        stuPreviewService.page(previewPage, wrapper);
        long total = previewPage.getTotal();//总记录数
        List<StuPreview> records = previewPage.getRecords();//数据list集合
        return R.ok().data("total", total).data("stuPreviewPage", records);
    }

    //5 根据某次课前预习查询该次练习下所有学生完成情况
    //教师端【某次课前预习学生情况】根据preview查询对应的stu_preview状态
    @ApiOperation(value = "某次课前预习学生情况")
    @GetMapping("/findPreviewById/{current}/{limit}/{id}")
    public R findPreviewById(@PathVariable long current,
                             @PathVariable long limit,
                             @PathVariable String id) {
        Preview preview = previewService.getById(id);
        int finish = stuPreviewService.findFinishCount(preview);
        int all = stuPreviewService.findAllCount(preview);
        String completion = finish+"/"+all;

        String toShowName = preview.getTitle();

        Page<StuPreview> previewPage = new Page<>(current, limit);
        //构造条件
        QueryWrapper<StuPreview> wrapper = new QueryWrapper<>();
        wrapper.eq("preview_id", id);
        //  排序
        wrapper.orderByDesc("finish");
        stuPreviewService.page(previewPage, wrapper);

        long total = previewPage.getTotal();//总记录数
        List<StuPreview> records = previewPage.getRecords();//数据list集合
        //显示的姓名集
        List<String> stuNameList = new ArrayList<>();
        for(StuPreview stuPreview: records){
            stuNameList.add(studentService.getStuInfoById(stuPreview.getStuId()).getName());
        }
        //找到所有满足章节和内容要求的stuPreview


        return R.ok().data("completion", completion).data("stuPreviewPageById", records).data("total",total).data("toShowName",toShowName).data("stuNameList",stuNameList);
    }

    //6 根据id查询练习本身
    // 教师端【课前预习内容修改】 传入Preview的Id
    @ApiOperation(value = "教师端修改课前预习-查询到该预习内容")
    @GetMapping("getPreview/{id}")
    public R getPreviewById(@PathVariable String id) {
        return R.ok().data("previewInfo", previewService.getById(id));
    }

    //7 修改课前预习
    // 教师端【课前预习编辑】 修改课前预习内容
    @ApiOperation(value = "教师端修改课前预习-课前预习更新")
    @PostMapping("/updatePreview")
    public R updatePreview(@RequestBody Preview preview) {
        System.out.println("查看教师端添加Preview时 "+preview.toString());
        preview.setGmtModified(new Date()); //设置更新时间
        boolean flag = previewService.updateById(preview);
        return flag ? R.ok() : R.error();
    }

    //8 删除课前预习
    // 教师端【课前预习删除】
    @ApiOperation(value = "逻辑删除课前预习")
    @DeleteMapping("/deletePreviewById/{id}")
    public R deletePreviewById(
            @ApiParam(name = "id", value = "课前预习ID", required = true)
            @PathVariable String id) {
        //删除课前预习，应该删除当前stuPreview中的所有该preview数据
        QueryWrapper<StuPreview> stuPreviewQueryWrapper = new QueryWrapper<>();
        stuPreviewQueryWrapper.eq("preview_id",id);
        stuPreviewService.remove(stuPreviewQueryWrapper);

        boolean flag = previewService.removeById(id);
        System.out.println(flag ? "删除成功" : "删除失败");
        return flag ? R.ok() : R.error();
    }

    //9 教师端添加课前预习
    //教师端【添加预习】时 自动生成随机题目集
    //前端封装一个Preview对象和count传到后端，根据Preview对象发布练习，为每个同学发放count个题目封装题目id到字符串中
    @ApiOperation(value = "教师端添加课前预习")
    @PostMapping("/addPreview")
    public R addPreview(@RequestBody Preview preview) {
        System.out.println("查看教师端添加Preview时 "+preview);
        //先把该题目保存到数据库
        preview.setGmtCreate(new Date());
        preview.setGmtModified(new Date());
        previewService.save(preview);

        int count = Integer.parseInt(preview.getCount()); //题目数量
        String chapterId = preview.getChapterId(); //章节id
        String sectionId = preview.getSectionId(); //小节ID
        List<String> stuIdList = studentService.findStuIdList(); //找到所有学生的id集合
        List<Topic> topicList = topicService.findAllTopicByChapterAndSection(chapterId, sectionId); //找到要求章节下的题目集合

        for (String stuId : stuIdList) {
            //为每个学生添加任务 放到stu_preview
            StuPreview stuPreview = new StuPreview();
            stuPreview.setStuId(stuId);
            stuPreview.setPreviewId(preview.getId());
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
            stuPreview.setTopics(topics.toString());
            stuPreview.setGmtFinish(null);
            stuPreview.setGmtDeadline(preview.getGmtDeadline());
            stuPreviewService.save(stuPreview);
        }
        return R.ok();
    }


    /**
     * ==========================学生端接口方法===========================
     */

    //10 提交课前预习
    // 学生端【完成练习】时 自动生成答案集传入
    @ApiOperation(value = "学生端添加完成后的课前预习")
    @PostMapping("/addStuPreview")
    public R addStuPreview(@RequestBody StuPreview stuPreview) {
        //需要前端将答案拼接处理好封装到stuPreview中传过来
        //如果前端不方便拼接字符串，封装到一个list中后端处理 目前默认为前端封装成字符串形式过来（A_ans_B形式）

        StuPreview baseStuPreview = stuPreviewService.getPreview(stuPreview);
        //将stuPreview换到base中去
        baseStuPreview.setStuAnswers(stuPreview.getStuAnswers());

        String[] topicIds = baseStuPreview.getTopics().split(" ");
        System.out.println("分割前的答案字符串" + baseStuPreview.getStuAnswers());
        //用户未作答的会以undefined传过来
        String[] ans = StringHandleUtils.splitAnswer(baseStuPreview.getStuAnswers(), topicIds.length);
        int score = StringHandleUtils.checkAnswers(topicIds, ans);

        baseStuPreview.setFinish(true);
        baseStuPreview.setScore(score);
        baseStuPreview.setGmtFinish(new Date());

        boolean save = stuPreviewService.updateById(baseStuPreview);
        return save ? R.ok() : R.error();

    }
}

