package com.introtoc.introService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.introtoc.introService.entity.Comment;
import com.introtoc.introService.entity.Student;
import com.introtoc.introService.mapper.CommentMapper;
import com.introtoc.introService.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.introtoc.introService.service.StudentService;
import com.introtoc.introService.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author tengsss
 * @since 2021-05-14
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private StudentService studentService;


    @Override
    public boolean addComment(Comment baseComment) {
        //新增新的评论
        Comment comment = new Comment();
        //先查询当前评论排序
        int size = findComments(baseComment.getTopicId()).size();
        //对当前题目评论数+1
        //复制
        comment.setContent(baseComment.getContent());
        comment.setGmtCreate(new Date());
        comment.setSort(size + 1);
        //将学生姓名封装到id里面去用于展示

        comment.setStuId(baseComment.getStuId());
        comment.setPraise(0);
        comment.setTopicId(baseComment.getTopicId());
        int insert = baseMapper.insert(comment);
        return insert > 0;
    }

    //找到该题目下的所有评论
    @Override
    public List<Comment> findComments(String topicId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("topic_id", topicId);
        wrapper.orderByAsc("sort");
//
        List<Comment> comments = baseMapper.selectList(wrapper);
        for (int i = 0; i < comments.size(); i++) {
            Student student = studentService.getById(comments.get(i).getStuId());
            comments.get(i).setStuId(student.getName());
        }
        return comments;
    }

    //点赞该评论
    @Override
    public boolean updateComment(Comment baseComment) {
        Comment comment = baseMapper.selectById(baseComment.getId());
        comment.setPraise(comment.getPraise() + 1);


        int update = baseMapper.updateById(comment);
        return update > 0;
    }


}
