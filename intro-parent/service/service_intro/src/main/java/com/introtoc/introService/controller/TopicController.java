package com.introtoc.introService.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.introtoc.commonUtils.R;
import com.introtoc.introService.entity.Student;
import com.introtoc.introService.entity.Topic;
import com.introtoc.introService.entity.chapter.OneChapter;
import com.introtoc.introService.service.*;
import com.introtoc.serviceBase.exceptionHandler.IntroException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.jsqlparser.statement.select.Top;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author tengsss
 * @since 2021-04-02
 */
@Api(description = "题目管理")
@RestController
@RequestMapping("/introService/topic")
@CrossOrigin
public class TopicController {

    //注入service
    @Autowired
    private TopicService topicService;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;

    //添加题目  学生端【新建题目】 教师端【添加题目】
    @ApiOperation(value = "添加题目")
    @PostMapping("/addTopic")
    public R addTopic(@RequestBody Topic topic) {
        System.out.println(topic.toString());
        topic.setGmtCreate(new Date());
        topic.setGmtModified(new Date());

        if (topic.getId() == null) {
            boolean save = topicService.save(topic);
            return save ? R.ok() : R.error();
        } else {
            QueryWrapper<Topic> wrapper = new QueryWrapper<>();
            wrapper.eq("id",topic.getId());
            boolean update = topicService.update(topic, wrapper);
            return update ? R.ok() : R.error();
        }
    }

    //根据提供者id进行查询题目
    @ApiOperation(value = "根据提供者id查询题目集")
    @GetMapping("/getTopicListByProId/{ProviderId}")
    public R getTopicListByProviderId(@PathVariable String ProviderId) {
        System.out.println(ProviderId);
        List<Topic> topicList = topicService.getTopicListByProviderId(ProviderId);
        List<String> chapterNameList = new ArrayList<>();
        List<String> sectionNameList = new ArrayList<>();
        //根据topic找到章节名称
        for (Topic t : topicList) {
            System.out.println(t.toString());
            String chapterName = chapterService.getById(t.getChapterId()).getTitle();
            chapterNameList.add(chapterName);
            t.setChapterId(chapterName);
            sectionNameList.add(sectionService.getById(t.getSectionId()).getTitle());
        }
        return R.ok().data("topicList", topicList).data("total", topicList.size())
                .data("chapterNameList",chapterNameList).data("sectionNameList",sectionNameList);
    }

    //根据题目id查询题目内容
    @ApiOperation(value = "根据题目的id来查询题目内容")
    @GetMapping("/getTopicById/{TopicId}")
    public R getTopicByTopicId(@PathVariable String TopicId) {
        System.out.println(TopicId);
        Topic topic = topicService.getById(TopicId);
        String provider;

        if(studentService.getStuInfoById(topic.getProviderId()) != null){ //如果是学生
            provider = studentService.getStuInfoById(topic.getProviderId()).getName();
        }else{ //如果是教师
            provider = teacherService.getTeacherById(topic.getProviderId()).getName();
        }
        return R.ok().data("topicInfo", topic).data("provider",provider);
    }

    //根据题目id删除题目 学生端【我的题目】—删除题目  教师端【题目管理】-删除题目
    @ApiOperation(value = "逻辑删除题目信息")
    @DeleteMapping("/deleteTopicById/{TopicId}")
    public R deleteStuById(
            @ApiParam(name = "TopicId", value = "题目ID", required = true)
            @PathVariable String TopicId) {
        System.out.println(TopicId);
        boolean flag = topicService.removeById(TopicId);
        System.out.println(flag ? "删除成功" : "删除失败");
        return flag ? R.ok() : R.error();
    }

    //分页查询所有数据
    //TODO
    @ApiOperation(value = "分页查询题目信息")
    @GetMapping("/pageTopic/{current}/{limit}")
    public R pageListTopic(@PathVariable long current,
                           @PathVariable long limit) {
        //创建page对象
        Page<Topic> pageTopic = new Page<>(current, limit);
        topicService.page(pageTopic, null);
        long total = pageTopic.getTotal(); //总记录数
        List<Topic> records = pageTopic.getRecords(); //获取查询后的数据页数
        List<String> chapterNameList = new ArrayList<>();
        List<String> sectionNameList = new ArrayList<>();
        //根据topic找到章节名称
        for (Topic t : records) {
            System.out.println(t.toString());
            String chapterName = chapterService.getById(t.getChapterId()).getTitle();
            chapterNameList.add(chapterName);
            t.setChapterId(chapterName);
            sectionNameList.add(sectionService.getById(t.getSectionId()).getTitle());
        }
        Map map = new HashMap();
        map.put("total", total);
        map.put("rows", records);
        map.put("chapterNameList",chapterNameList);
        map.put("sectionNameList",sectionNameList);
        return R.ok().data(map);
    }

    //根据更新时间进行排序分页 主页【题目广场】—最新题目
    //TODO
    @ApiOperation(value = "分页查询题目信息")
    @GetMapping("/pageTopicByUpdate/{current}/{limit}")
    public R pageTopicByUpdate(@PathVariable long current,
                               @PathVariable long limit) {
        //创建page对象
        Page<Topic> pageTopicByUpdate = new Page<>(current, limit);
        //创建查询对象
        QueryWrapper<Topic> wrapperByUpdate = new QueryWrapper<>();
        wrapperByUpdate.orderByDesc("gmt_modified");
        topicService.page(pageTopicByUpdate, wrapperByUpdate);
        long total = pageTopicByUpdate.getTotal(); //总记录数
        List<Topic> records = pageTopicByUpdate.getRecords(); //获取查询后的数据页数
        //根据topic找到章节名称
        for (Topic t : records) {
            t.setChapterId(chapterService.getById(t.getChapterId()).getTitle());
            t.setSectionId(sectionService.getById(t.getSectionId()).getTitle());
        }
        System.out.println(records.toString());
        Map map = new HashMap();
        map.put("total", total);
        map.put("rows", records);

        return R.ok().data(map);
    }

    //查询每个章节下的题目数量进行展示
    @ApiOperation(value = "查询每个章节下的题目数量进行展示")
    @GetMapping("/findCountPerChapter")
    public R findCountPerChapter() {
        List<Topic> records = topicService.list(null); //获取查询后的数据页数
        HashMap<String, Integer> chapterInfoMap = new HashMap<>();
        for (Topic t : records) {
            String chapterName = chapterService.getById(t.getChapterId()).getTitle();
            chapterName = chapterName.substring(0, 3);
            chapterInfoMap.put(chapterName, chapterInfoMap.getOrDefault(chapterName, 0) + 1);
        }
        return R.ok().data("chapterInfo", chapterInfoMap.keySet()).data("chapterData", chapterInfoMap.values());
    }

    //根据评论数量进行排序  主页【题目广场】—热点题目 需登录才能看到评论
    //思路：遍历评论列表，找到评论的题目id进行计数
    //TODO
    //分页查询所有数据
    //TODO
    @ApiOperation(value = "分页查询题目信息")
    @GetMapping("/pageHotListTopic/{current}/{limit}")
    public R pageHotListTopic(@PathVariable long current,
                           @PathVariable long limit) {
        //创建page对象
        Page<Topic> pageTopic = new Page<>(current, limit);
        QueryWrapper<Topic> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("comment_count");
        topicService.page(pageTopic, wrapper);
        long total = pageTopic.getTotal(); //总记录数
        List<Topic> records = pageTopic.getRecords(); //获取查询后的数据页数

        Map map = new HashMap();
        map.put("total", total);
        map.put("rows", records);

        return R.ok().data(map);
    }

    @ApiOperation(value = "分页查询题目库中信息")
    @GetMapping("/adoptedTopicPageList/{current}/{limit}")
    public R getAdoptedTopicPageList(@PathVariable long current,
                                     @PathVariable long limit
                                     ){
        //创建page对象
        Page<Topic> pageTopic = new Page<>(current, limit);
        QueryWrapper<Topic> wrapper = new QueryWrapper<>();
        wrapper.eq("adopted",1);
        topicService.page(pageTopic, wrapper);
        long total = pageTopic.getTotal(); //总记录数
        List<Topic> records = pageTopic.getRecords(); //获取查询后的数据页数
        List<String> chapterNameList = new ArrayList<>();
        List<String> sectionNameList = new ArrayList<>();
        //根据topic找到章节名称
        for (Topic t : records) {
            System.out.println(t.toString());
            String chapterName = chapterService.getById(t.getChapterId()).getTitle();
            chapterNameList.add(chapterName);
            t.setChapterId(chapterName);
            sectionNameList.add(sectionService.getById(t.getSectionId()).getTitle());
        }
        return R.ok().data("topicList", records).data("total", total)
                .data("chapterNameList",chapterNameList).data("sectionNameList",sectionNameList);

    }

}

