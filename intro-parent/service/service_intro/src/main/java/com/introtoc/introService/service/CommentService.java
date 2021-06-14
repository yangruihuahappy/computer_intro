package com.introtoc.introService.service;

import com.introtoc.introService.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tengsss
 * @since 2021-05-14
 */
public interface CommentService extends IService<Comment> {

    boolean addComment(Comment baseComment);

    List<Comment> findComments(String topicId);

    boolean updateComment(Comment baseComment);
}
