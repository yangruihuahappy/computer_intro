package com.introtoc.introService.controller;


import com.introtoc.commonUtils.R;
import com.introtoc.introService.entity.Chapter;
import com.introtoc.introService.entity.chapter.OneChapter;
import com.introtoc.introService.service.ChapterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tengsss
 * @since 2021-03-31
 */
@Api(description="章节管理")
@RestController
@RequestMapping("/introService/chapter")
@CrossOrigin
public class ChapterController {

    //注入service
    @Autowired
    private ChapterService chapterService;

    //添加章节  教师端和学生端暂时用不到，只用作快速向数据库插入数据
    @ApiOperation(value = "添加章节")
    @PostMapping("/addChapter")
    public R addChapter(@RequestBody Chapter chapter){
        boolean save = chapterService.save(chapter);
        return save?R.ok():R.error();
    }

    //获取所有章节信息 【知识点】目录 将所有小节信息封装到章节中
    @ApiOperation(value = "查询所有章节")
    @GetMapping("/getAllChapter")
    public R getAllChapter(){
        //list集合中包含一级目录，在一级目录中封装了二级目录
        System.out.println("进行到了查询所有章节");
        List<OneChapter> chapterList = chapterService.getAllOneAndTwoChapter();
        System.out.println(chapterList.toString());
        return R.ok().data("chapterList",chapterList);
    }

}

