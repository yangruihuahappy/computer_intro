package com.introtoc.introService.controller.practice;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.introtoc.commonUtils.R;
import com.introtoc.introService.entity.*;
import com.introtoc.introService.entity.query.HomeworkQuery;
import com.introtoc.introService.entity.query.TimedtestQuery;
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
 *  前端控制器
 * </p>
 *
 * @theme 随堂测验管理
 * @author tengsss
 * @since 2021-04-14
 */
@Api(description="随堂测验管理")
@RestController
@RequestMapping("/introService/timedtest")
@CrossOrigin
public class TimedtestController {

    //注入service
    @Autowired
    private TimedtestService timedtestService;
    @Autowired
    private StuTimedtestService stuTimedtestService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private SectionService sectionService;

    /**==========================公共接口方法=========================== */

    //1 查询某个学生的所有测验情况
    // 教师端【查看某个学生完成情况】
    // 学生端【我的随堂测验】
    @ApiOperation(value = "查询学生所有随堂测验")
    @PostMapping("/findTimedtestByStudent/{current}/{limit}/{stuId}")
    public R findTimedtestByStudent(@PathVariable long current,
                                     @PathVariable long limit,
                                     @PathVariable String stuId,
                                     @RequestBody(required = false) TimedtestQuery timedtestQuery) {

        //多条件组合
        System.out.println(timedtestQuery.toString());
        String title = timedtestQuery.getTitle();
        String chapterId = timedtestQuery.getChapter();
        String sectionId = timedtestQuery.getSection();
        String begin = timedtestQuery.getBegin();
        String end = timedtestQuery.getEnd();
        //这三个部分是preview里面的限制条件。不能用在stu_preview
        List<Timedtest> timedtestList;
        QueryWrapper<Timedtest> wrapperTimed = new QueryWrapper<>();
        //判断 加入构造条件
        if (!StringUtils.isEmpty(title)) {
            //判断标题不为空 加入条件
            wrapperTimed.like("title", title);
        }
        if (!StringUtils.isEmpty(chapterId)) {
            //判断标题不为空 加入条件
            wrapperTimed.eq("chapter_id", chapterId);
        }
        if (!StringUtils.isEmpty(sectionId)) {
            //判断标题不为空 加入条件
            wrapperTimed.eq("section_id", sectionId);
        }if (!StringUtils.isEmpty(begin)) {
            //构建条件
            wrapperTimed.ge("gmt_create", begin);
        }
        if (!StringUtils.isEmpty(end)) {
            //构建条件
            wrapperTimed.le("gmt_deadline", end);
        }
        timedtestList = timedtestService.list(wrapperTimed);//获取所有满足条件的preview
        //再找所有满足条件的stu_preview
        QueryWrapper<StuTimedtest> wrapper = new QueryWrapper<>();
        wrapper.eq("stu_id", stuId);
        List<StuTimedtest> allTimedtest = stuTimedtestService.list(wrapper); //找到该学生的所有preview
        List<StuTimedtest> stuTimedtestList = new ArrayList<>();
        //显示的标题集
        List<String> toShowNameList = new ArrayList<>();
        //找到所有满足章节和内容要求的stuPreview
        for (Timedtest t : timedtestList) {
            for (StuTimedtest st : allTimedtest) {
                if (t.getId().equals(st.getTimedtestId())) {
                    stuTimedtestList.add(st);
                    toShowNameList.add(t.getTitle());
                }
            }
        }
        //创建Page对象
        Page<StuTimedtest> stuTimedtestPage = new Page<>(current, limit);
        stuTimedtestPage.setRecords(stuTimedtestList);
        stuTimedtestPage.setSize(limit);
        stuTimedtestPage.setTotal(stuTimedtestList.size());
        stuTimedtestPage.setCurrent(current);
        long total = stuTimedtestList.size();//总记录数
        List<StuTimedtest> records = stuTimedtestPage.getRecords();//数据list集合

        return R.ok().data("total", total).data("stuTimedtestPage", records).data("toShowName",toShowNameList);

    }

    //2 根随堂测验id查看详细信息 包括题目
    // 学生端【查看完成练习】
    // 教师端【查看某个学生练习】
    @ApiOperation(value = "查询随堂测验")
    @GetMapping("/findStuTimedtestById/{id}")
    public R findStuTimedtestById(@PathVariable String id){ //此id为stuTimedtest id 查看详情用
        StuTimedtest stuTimedtestInfo = stuTimedtestService.getById(id);
        //将题目列表取出来封装到list中
        List<Topic> topicList = new ArrayList<>();
        String topics = stuTimedtestInfo.getTopics();
        String[] topicsArr = topics.split(" ");
        for(String topicId:topicsArr){
            topicList.add(topicService.getById(topicId));
        }
        //将学生的答案集封装到list中
        List<String> answerList = new ArrayList<>();
        String answer = stuTimedtestInfo.getStuAnswers();
        if(answer!=null && stuTimedtestInfo.getFinish()){
            String[] ansArr = answer.split("_ans_"); //在回答中可能会出现空格的情况，我们使用_ans_来隔开每一个答案
            answerList.addAll(Arrays.asList(ansArr));
        }

        return R.ok().data("stuTimedtestInfo",stuTimedtestInfo).data("topicList",topicList).data("answerList",answerList);
    }

    /**==========================教师端接口方法=========================== */

    //3 查询已发布所有显示测试
    // 教师端【课后作业管理】 封装好查询实体类
    @ApiOperation(value = "教师端查询所有随堂测验")
    @GetMapping("/findTimedtestPage/{current}/{limit}")
    public R findTimedtestPage(@PathVariable long current,
                              @PathVariable long limit){
        //创建Page对象
        Page<Timedtest> timedtestPage = new Page<>(current,limit);
        //构建条件
        QueryWrapper<Timedtest> wrapper = new QueryWrapper<>();

        //  排序
        wrapper.orderByDesc("gmt_create");

        //调用方法实现查询分页
        timedtestService.page(timedtestPage,wrapper);

        long total = timedtestPage.getTotal();//总记录数
        List<Timedtest> records = timedtestPage.getRecords();//数据list集合
        //将完成情况赋值给数组
        List<String> toShowCompletion = new ArrayList<>();
        List<String> chapterNameList = new ArrayList<>();
        List<String> sectionNameList = new ArrayList<>();
        if (records != null) {
            for (Timedtest t : records) {
                String chapterName = chapterService.getById(t.getChapterId()).getTitle();
                chapterNameList.add(chapterName);
                sectionNameList.add(sectionService.getById(t.getSectionId()).getTitle());
                //找到每个preview下所有完成学生情况
                int finish = stuTimedtestService.findFinishCount(t);
                int all = stuTimedtestService.findAllCount(t);
                toShowCompletion.add(finish+"/"+all);
            }
        }

        return R.ok().data("total",total).data("timedtestList",records).data("completion",toShowCompletion)
                .data("chapterNameList",chapterNameList).data("sectionNameList",sectionNameList);
    }

    //4 查询将截至事件
    // 教师端【紧急任务】按截止时间查询未完成
    @ApiOperation(value = "教师端查询紧急任务")
    @GetMapping("/findDeadline/{current}/{limit}")
    public R findDeadlineTimedtest(@PathVariable long current,
                                  @PathVariable long limit){
        //按截止时间进行倒叙，要求完成状态为未完成
        Page<StuTimedtest> stuTimedtestPage = new Page<>(current,limit);
        //构造条件
        QueryWrapper<StuTimedtest> wrapper = new QueryWrapper<>();
        wrapper.eq("finish",0);
        //  排序
        wrapper.orderByDesc("gmt_deadline");
        stuTimedtestService.page(stuTimedtestPage,wrapper);
        long total = stuTimedtestPage.getTotal();//总记录数
        List<StuTimedtest> records = stuTimedtestPage.getRecords();//数据list集合
        return R.ok().data("total",total).data("stuHomeworkPage",records);
    }

    //5 查询某次 homework下的所有stuHomework
    // 教师端【查询某次所有学生课后作业】
    @ApiOperation(value = "查询某次随堂测验下所有练习信息")
    @GetMapping("/findStuTimedtest/{current}/{limit}/{id}")
    public R findStuTimedtestByTimedtestId(@PathVariable long current,
                                         @PathVariable long limit,
                                         @PathVariable String id){

        Timedtest timedtest = timedtestService.getById(id);
        int finish = stuTimedtestService.findFinishCount(timedtest);
        int all = stuTimedtestService.findAllCount(timedtest);
        String completion = finish+"/"+all;

        String toShowName = timedtest.getTitle();

        Page<StuTimedtest> stuTimedtestPage = new Page<>(current,limit);
        //构造条件
        QueryWrapper<StuTimedtest> wrapper = new QueryWrapper<>();
        wrapper.eq("timedtest_id",id);
        //  排序
        wrapper.orderByDesc("finish");
        stuTimedtestService.page(stuTimedtestPage,wrapper);
        long total = stuTimedtestPage.getTotal();//总记录数
        List<StuTimedtest> records = stuTimedtestPage.getRecords();//数据list集合

        //显示的姓名集
        List<String> stuNameList = new ArrayList<>();
        for(StuTimedtest stuTimedtest: records){
            System.out.println("==================");
            System.out.println(studentService.getStuInfoById(stuTimedtest.getStuId()).getName());
            stuNameList.add(studentService.getStuInfoById(stuTimedtest.getStuId()).getName());
        }

        return R.ok().data("total",total).data("stuTimedtestPageById",records).data("completion", completion).data("toShowName",toShowName).data("stuNameList",stuNameList);
    }

    //6 修改课后作业信息
    // 教师端【修改课后作业】
    //6.1 现根据id查询某个课后作业进行回显
    @ApiOperation(value = "教师端修改随堂测验——查询到该作业内容")
    @GetMapping("getTimedtest/{id}")
    public R getTimedtestById(@PathVariable String id){
        return R.ok().data("timedtestInfo",timedtestService.getById(id));
    }

    //6.2 之后保存课程信息
    @ApiOperation(value = "教师端修改随堂测验-随堂测验更新")
    @PostMapping("/updateTimedtest")
    public R updateTimedtest(@RequestBody Timedtest timedtest){
        timedtest.setGmtModified(new Date()); //设置更新时间
        boolean flag = timedtestService.updateById(timedtest);
        return flag?R.ok():R.error();
    }

    //7 删除课前预习
    // 教师端【题目删除】
    @ApiOperation(value="逻辑删除随堂测验")
    @DeleteMapping("/deleteTimedtestById/{id}")
    public R deleteTimedtestById(
            @ApiParam(name = "id", value = "课后作业ID", required = true)
            @PathVariable String id){
        //删除课前预习，应该删除当前stuTimedtest中的所有该timedtest数据

        QueryWrapper<StuTimedtest> stuPreviewQueryWrapper = new QueryWrapper<>();
        stuPreviewQueryWrapper.eq("timedtest_id",id);
        stuTimedtestService.remove(stuPreviewQueryWrapper);
        //用nacos去oss服务器进行相应的视频删除
        boolean flag = timedtestService.removeById(id);
        System.out.println(flag?"删除成功":"删除失败");
        return flag?R.ok():R.error();
    }

    //8 教师端添加课后作业 完成Timedtest的基本信息填写后，自动发放给每位同学到stuTimedtest表中
    // 教师端【添加课后作业】
    @ApiOperation(value = "教师端添加随堂测验")
    @PostMapping("/addTimedtest")
    public R addTimedtest(@RequestBody Timedtest timedtest){

        //先把该基本信息保存到timedtest表
        timedtest.setGmtCreate(new Date());
        timedtest.setGmtModified(new Date());
        timedtestService.save(timedtest);

        int count = timedtest.getCount(); //题目数量
        String chapterId = timedtest.getChapterId(); //章节id
        String sectionId = timedtest.getSectionId(); //小节ID
        List<String> stuIdList = studentService.findStuIdList(); //找到所有学生的id集合
        List<Topic> topicList = topicService.findAllTopicByChapterAndSection(chapterId,sectionId); //找到要求章节下的题目集合

        for(String stuId:stuIdList){
            //为每个学生添加任务 放到stu_preview
            StuTimedtest stuTimedtest = new StuTimedtest();
            stuTimedtest.setStuId(stuId);
            stuTimedtest.setTimedtestId(timedtest.getId());
            List<String> topicIds = new ArrayList<>();
            //根据题目集生产随机一组
            StringBuilder topics = new StringBuilder();
            for(int i=0;i<count;i++){
                int index = (int) (Math.random()*topicList.size());
                String tempId = topicList.get(index).getId();
                if(topicIds.contains(tempId)){
                    i--;
                }else{
                    topicIds.add(tempId);
                }
            }
            for(String topicId:topicIds){
                topics.append(topicId).append(" ");
            }
            stuTimedtest.setTopics(topics.toString());
            stuTimedtest.setGmtFinish(null);
            stuTimedtest.setGmtDeadline(timedtest.getGmtDeadline());
            stuTimedtestService.save(stuTimedtest);
        }
        return R.ok();
    }

    /**==========================学生端接口方法=========================== */

    //9 学生完成练习提交 学生端【提交作业】 相当于修改stuHomework的信息
    @ApiOperation(value = "学生端添加完成后的课后作业")
    @PostMapping("/updateStuTimedtest")
    public R updateStuTimedtest(@RequestBody StuTimedtest stuTimedtest){
        System.out.println(stuTimedtest.toString());
        //需要前端将答案拼接处理好封装到stuHomework中传过来
        //如果前端不方便拼接字符串，封装到一个list中后端处理 目前默认为前端封装成字符串形式过来（A_ans_B形式）
        StuTimedtest baseStuTimedtest = stuTimedtestService.getTimedtest(stuTimedtest);
        System.out.println(baseStuTimedtest.toString());
        //将stuPreview换到base中去
        baseStuTimedtest.setStuAnswers(stuTimedtest.getStuAnswers());

        String[] topicIds = baseStuTimedtest.getTopics().split(" ");

        String[] ans = StringHandleUtils.splitAnswer(baseStuTimedtest.getStuAnswers(),topicIds.length);
        int score = StringHandleUtils.checkAnswers(topicIds,ans);

        baseStuTimedtest.setFinish(true);
        baseStuTimedtest.setScore(score);
        baseStuTimedtest.setGmtFinish(new Date());

        boolean save = stuTimedtestService.updateById(baseStuTimedtest);
        return save?R.ok():R.error();

    }


}

