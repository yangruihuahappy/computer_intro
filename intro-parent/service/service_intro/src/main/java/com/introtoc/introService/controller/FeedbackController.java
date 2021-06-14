package com.introtoc.introService.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.introtoc.commonUtils.R;
import com.introtoc.introService.entity.Feedback;
import com.introtoc.introService.service.FeedbackService;
import com.introtoc.introService.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tengsss
 * @since 2021-04-25
 */
@Api(description = "反馈意见")
@RestController
@RequestMapping("/introService/feedback")
@CrossOrigin
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private StudentService studentService;

    //1 上传反馈意见
    @ApiOperation(value = "学生上传反馈意见")
    @PostMapping("/publish")
    public R publishFeedback(@RequestBody Feedback feedback){
        System.out.println(feedback.toString());
        feedback.setGmtCreate(new Date());
        feedbackService.save(feedback);
        return R.ok();
    }

    //查询所有反馈意见】
    @ApiOperation(value = "教师查询所有反馈意见")
    @GetMapping("/getList/{current}/{limit}")
    public R getFeedbackList(@PathVariable long current,
                             @PathVariable long limit){
        //创建Page对象
        Page<Feedback> feedbackPage = new Page<>(current, limit);
        QueryWrapper<Feedback> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("gmt_create");
        feedbackService.page(feedbackPage,wrapper);
        long total = feedbackPage.getTotal();//总记录数
        List<Feedback> records = feedbackPage.getRecords();//数据list集合
        List<String> nameList = new ArrayList<>();
        for(Feedback f:records){
            if(f.getAnonymous()){
                nameList.add("匿名用户");
            }else{
                nameList.add(studentService.getStuInfoById(f.getStuId()).getName());
            }

        }

        return R.ok().data("total",total).data("feedbackList",records).data("nameList",nameList);
    }

}

