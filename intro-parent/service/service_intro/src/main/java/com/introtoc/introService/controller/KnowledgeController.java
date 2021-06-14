package com.introtoc.introService.controller;


import com.introtoc.commonUtils.R;
import com.introtoc.introService.Client.VideoClient;
import com.introtoc.introService.entity.Knowledge;
import com.introtoc.introService.service.KnowledgeService;
import com.introtoc.introService.service.SectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tengsss
 * @since 2021-03-31
 */

@Api(description="知识点管理")
@RestController
@RequestMapping("/introService/knowledge")
@CrossOrigin
public class KnowledgeController {

    //注入service
    @Autowired
    private KnowledgeService knowledgeService;

    //注入vodClient
    @Autowired
    private VideoClient videoClient;

    //添加知识点  教师端和学生端暂时用不到，只用作快速向数据库插入数据
    @ApiOperation(value = "添加知识点")
    @PostMapping("/addKnowledge")
    public R addChapter(@RequestBody Knowledge knowledge){
        knowledge.setGmtCreate(new Date());
        knowledge.setGmtModified(new Date());
        System.out.println(knowledge.toString());
        boolean save = knowledgeService.save(knowledge);
        return save?R.ok():R.error();
    }

    //根据id查询知识点
    @ApiOperation(value = "根据id查询")
    @GetMapping("/getKnowledge/{id}")
    public R getKnowledge(@PathVariable String id){
        Knowledge knowledgeInfo = knowledgeService.getKnowInfoById(id);
        return R.ok().data("knowledgeInfo",knowledgeInfo);
    }


}

