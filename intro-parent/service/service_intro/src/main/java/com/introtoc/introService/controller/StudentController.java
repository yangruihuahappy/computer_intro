package com.introtoc.introService.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.introtoc.commonUtils.R;
import com.introtoc.introService.entity.*;
import com.introtoc.introService.entity.chapter.OneChapter;
import com.introtoc.introService.entity.query.StudentQuery;
import com.introtoc.introService.entity.query.ToDoQuery;
import com.introtoc.introService.entity.query.UrgentQuery;
import com.introtoc.introService.service.*;
import com.introtoc.serviceBase.exceptionHandler.IntroException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author tengsss
 * @since 2021-03-30
 */
@Api(description = "学生管理")
@RestController
@RequestMapping("/introService/student")
@CrossOrigin
public class StudentController {

    //本学期开始日期
    private final String startDate = "2021-03-01";

    @Autowired
    private StudentService studentService;
    @Autowired
    private PreviewService previewService;
    @Autowired
    private StuPreviewService stuPreviewService;
    @Autowired
    private HomeworkService homeworkService;
    @Autowired
    private StuHomeworkService stuHomeworkService;
    @Autowired
    private TimedtestService timedtestService;
    @Autowired
    private StuTimedtestService stuTimedtestService;
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private StuExperimentService stuExperimentService;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private SummaryService summaryService;
    @Autowired
    private StuSummaryService stuSummaryService;


    //根据id查询学生信息  学生端用于【主页展示】  教师端用于【查询学生信息】
    @ApiOperation(value = "根据id查询学生信息")
    @GetMapping("/getStuInfo/{stuId}")
    public R getStudentInfo(@PathVariable String stuId) {
        Student stuInfo = studentService.getStuInfoById(stuId);
        System.out.println(stuInfo.toString());

        return R.ok().data("stuInfo", stuInfo);
    }

    //查询所有信息  教师端【学生管理】
    @ApiOperation(value = "查询所有学生信息")
    @GetMapping("/findAll")
    public R findAll() {
        //调用service的方法查询所有学生信息
        List<Student> list = studentService.list(null);
        return R.ok().data("stuList", list);
    }


    //分页查询学生信息  教师端【学生管理】
    @ApiOperation(value = "分页查询学生信息")
    @PostMapping("/pageStudent/{current}/{limit}")
    public R pageListStudent(@PathVariable long current,
                             @PathVariable long limit,
                             @RequestBody(required = false) StudentQuery studentQuery) {
        System.out.println("5.21找bug");
        //多条件组合
        String stuNum = studentQuery.getStuNum();
        String name = studentQuery.getName();
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(stuNum)) {
            //判断标题不为空 加入条件
            wrapper.like("stu_num", stuNum);
        }
        if (!StringUtils.isEmpty(name)) {
            //判断标题不为空 加入条件
            wrapper.like("name", name);
        }

        //创建page对象
        Page<Student> pageStudent = new Page<>(current, limit);
        studentService.page(pageStudent, wrapper);
        long total = pageStudent.getTotal(); //总记录数
        List<Student> records = pageStudent.getRecords(); //获取查询后的数据页数
        Map map = new HashMap();
        map.put("total", total);
        map.put("studentList", records);

        return R.ok().data(map);
    }

    //添加学生信息    //教师端用【学生管理】
    @ApiOperation(value = "添加学生信息")
    @PostMapping("/addStu")
    public R addStudent(@RequestBody Student student) {
        student.setPassword(student.getStuNum());
        student.setGmtCreate(new Date());
        student.setGmtModified(new Date());
        boolean save = studentService.save(student);
        return save ? R.ok() : R.error().message("添加个人信息失败");
    }

    //批量添加学生信息  教师端【批量导入】用于导入excel文件自动读取添加
    @ApiOperation(value = "批量添加学生信息")
    @PostMapping("/addStuBatch")
    public R addStudentBatch(MultipartFile file) {
        //上传的excel文件
        studentService.saveStudentBatch(file, studentService);
        return R.ok();
    }

    //删除学生信息 教师端【学生管理】
    @ApiOperation(value = "逻辑删除学生信息")
    @DeleteMapping("/deleteStuById/{stuId}")
    public R deleteStuById(
            @ApiParam(name = "stuId", value = "学生ID", required = true)
            @PathVariable String stuId) {
        System.out.println(stuId);
        //先处理该学生的所有练习和总结信息
        QueryWrapper<StuPreview> stuPreviewQueryWrapper = new QueryWrapper<>();
        QueryWrapper<StuHomework> stuHomeworkQueryWrapper = new QueryWrapper<>();
        QueryWrapper<StuTimedtest> stuTimedtestQueryWrapper = new QueryWrapper<>();
        QueryWrapper<StuExperiment> stuExperimentQueryWrapper = new QueryWrapper<>();
        QueryWrapper<StuSummary> stuSummaryQueryWrapper = new QueryWrapper<>();
        stuPreviewQueryWrapper.eq("stu_id",stuId);
        stuHomeworkQueryWrapper.eq("stu_id",stuId);
        stuTimedtestQueryWrapper.eq("stu_id",stuId);
        stuExperimentQueryWrapper.eq("stu_id",stuId);
        stuSummaryQueryWrapper.eq("stu_id",stuId);
        stuPreviewService.remove(stuPreviewQueryWrapper);
        stuHomeworkService.remove(stuHomeworkQueryWrapper);
        stuTimedtestService.remove(stuTimedtestQueryWrapper);
        stuExperimentService.remove(stuExperimentQueryWrapper);
        stuSummaryService.remove(stuSummaryQueryWrapper);

        boolean flag = studentService.removeById(stuId);
        System.out.println(flag ? "删除成功" : "删除失败");
        return flag ? R.ok() : R.error();
    }

    //修改学生信息 学生端【修改个人信息】 教师端【修改学生信息】
    @ApiOperation(value = "更新学生信息")
    @PostMapping("/updateStu")
    public R updateStudent(@RequestBody Student student) {
        student.setGmtModified(new Date()); //设置更新时间
        boolean flag = studentService.updateById(student);
        return flag ? R.ok() : R.error();
    }

    //查询某个学生所有四种练习和总结 待办事项
    @ApiOperation(value = "查询学生所有待办事项")
    @PostMapping("/stuToDoList")
    public R findAllToDo(@RequestBody(required = false) Student student) {
        System.out.println(student.toString());
        Student stu = studentService.getById(student);
        List<ToDoQuery> todoList = studentService.setToDoList(stu); //用于返回数据的对象集合
        return R.ok().data("todoList", todoList);
    }

    //查询某个学生的紧急事务
    @ApiOperation(value = "查询某个学生紧急任务")
    @PostMapping("/getUrgentList")
    public R getUrgentList(@RequestBody(required = false) UrgentQuery query) {
        System.out.println(query.toString());
        String stuId = query.getStuId();
        String type = query.getType();
        Student student = studentService.getById(stuId);
        List<ToDoQuery> urgentList;
        if (type != null && null != stuId) {
            urgentList = studentService.getUrgent(student, type);
        } else {
            throw new IntroException(20001, "访问类别有误");
        }

        return R.ok().data("urgentList", urgentList);
    }

    //查询某个学生已经错过且未完成的任务
    @ApiOperation(value = "查询某学生未完成已错过的任务")
    @PostMapping("/getMissedList")
    public R getMissedList(@RequestBody(required = false) Student student) {
        Student stu = studentService.getById(student);
        List<ToDoQuery> MissedList = studentService.setMissedList(stu); //用于返回数据的对象集合
        return R.ok().data("MissedList", MissedList);
    }

    //我的成就 返回横坐标和当前所有练习的分数集合
    @ApiOperation(value = "用于我的成就用于展示分数")
    @GetMapping("/getAllPracticeScore/{stuId}")
    public R getAllPracticeScore(@PathVariable String stuId) {

        //查找该学生所有的课前预习分数封装到previewScoreList
        List<Integer> previewScoreList = new ArrayList<>();
        //查找该学生所有的课前预习分数封装到homeworkScoreList
        List<Integer> homeworkScoreList = new ArrayList<>();
        //查找该学生所有的课前预习分数封装到homeworkScoreList
        List<Integer> TimedtestScoreList = new ArrayList<>();
        //查找该学生所有的课前预习分数封装到homeworkScoreList
        List<Integer> ExperimentScoreList = new ArrayList<>();

        //先找到最后的发布的
        List<String> indexList = new ArrayList<>();
        List<OneChapter> chapterList = chapterService.getAllOneAndTwoChapter();
        for (int i = 0; i < chapterList.size(); i++) {
            for (int j = 0; j < chapterList.get(i).getChildren().size(); j++) {

                Preview preview = previewService.findPreview(stuId, chapterList.get(i).getChildren().get(j).getId());
                Homework homework = homeworkService.findHomework(stuId, chapterList.get(i).getChildren().get(j).getId());
                Timedtest timedtest = timedtestService.findTimedtest(stuId, chapterList.get(i).getChildren().get(j).getId());
                Experiment experiment = experimentService.findExperiment(stuId, chapterList.get(i).getChildren().get(j).getId());
                if (preview != null || homework != null || timedtest != null || experiment != null) { //确保该章节下有成绩
                    indexList.add("第" + (i + 1) + "章第" + (j + 1) + "节");
                    if (preview != null) {
                        previewScoreList.add(stuPreviewService.findScore(preview, stuId));
                    } else {
                        previewScoreList.add(0); //传入小节id和学生id即可
                    }
                    if (homework == null) {
                        homeworkScoreList.add(0);
                    } else {
                        homeworkScoreList.add(stuHomeworkService.findScore(homework, stuId)); //传入小节id和学生id即可
                    }
                    if (timedtest == null) {
                        TimedtestScoreList.add(0);
                    } else {
                        TimedtestScoreList.add(stuTimedtestService.findScore(timedtest, stuId)); //传入小节id和学生id即可
                    }
                    if (experiment == null) {
                        ExperimentScoreList.add(0);
                    } else {
                        ExperimentScoreList.add(stuExperimentService.findScore(experiment, stuId)); //传入小节id和学生id即可
                    }
                }
            }
        }
        System.out.println(indexList.toString());
        System.out.println(previewScoreList.toString());
        return R.ok().data("indexList", indexList)
                .data("previewScoreList", previewScoreList)
                .data("homeworkScoreList", homeworkScoreList)
                .data("timedtestScoreList", TimedtestScoreList)
                .data("experimentScoreList", ExperimentScoreList);

    }

    //我的成就 返回横坐标和当前所有练习的分数集合
    @ApiOperation(value = "用于我的成就用于展示分数")
    @GetMapping("/getAllSummaryScore/{stuId}")
    public R getAllSummaryScore(@PathVariable String stuId) {


        //查询所有已发布的每周总结

        List<Summary> summaryList = summaryService.list(null);
        //用来存所有分数集合
        int[] summaryScoreArr = new int[summaryList.size()];
        String[] indexArr = new String[summaryList.size()];

        for (Summary summary : summaryList) {
            //查找第几周放置在横坐标中
            int index;
            String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(summary.getDescription());
            index = Integer.parseInt(m.replaceAll("").trim());
            System.out.println(index);
            indexArr[index - 1] = "第" + index + "周";
            //加分数
            StuSummary stuSummary = stuSummaryService.findOne(summary.getId(), stuId);
            summaryScoreArr[index - 1] = stuSummary.getScore();
        }

        return R.ok().data("indexList",Collections.singletonList(indexArr)).data("summaryScoreList", Collections.singletonList(summaryScoreArr));
    }
}

