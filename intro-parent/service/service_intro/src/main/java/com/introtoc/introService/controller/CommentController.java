package com.introtoc.introService.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.introtoc.commonUtils.R;
import com.introtoc.introService.entity.Comment;
import com.introtoc.introService.entity.Topic;
import com.introtoc.introService.service.CommentService;
import com.introtoc.introService.service.TopicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author tengsss
 * @since 2021-05-14
 */
@Api(description = "评论管理")
@RestController
@RequestMapping("/introService/comment")
@CrossOrigin
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private TopicService topicService;

    //添加评论
    @ApiOperation(value = "在当前题目下添加评论")
    @PostMapping("/addOrUpdateComment")
    public R addOrUpdateComment(@RequestBody Comment baseComment) {
        boolean success = false;
        //判断新增还是更新
        if (baseComment.getId() == null) { //新增
            success = commentService.addComment(baseComment);
            if(success){
                Topic topic = topicService.getById(baseComment.getTopicId());
                topic.setCommentCount(topic.getCommentCount()+1);
                System.out.println("评论相关："+topic.getCommentCount());
                topicService.updateById(topic);
            }
        } else { //因为评论不支持编辑操作，故此处的编辑操作定为点赞改变评论信息
            success = commentService.updateComment(baseComment);
        }


        return success? R.ok():R.error();
    }

    //查询当前topic下的评论 传入topicId 并按sort返回所有评论
    @ApiOperation(value = "查找题目下的评论")
    @GetMapping("/findCommentsByTopicId/{topicId}")
    public R findCommentsByTopicId(@PathVariable String topicId) {

        List<Comment> commentList = commentService.findComments(topicId);
        System.out.println(commentList.toString());
        return R.ok().data("commentList", commentList);
    }


}

