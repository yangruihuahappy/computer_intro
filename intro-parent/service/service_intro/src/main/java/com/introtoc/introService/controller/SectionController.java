package com.introtoc.introService.controller;


import com.introtoc.commonUtils.R;
import com.introtoc.introService.entity.Section;
import com.introtoc.introService.service.SectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tengsss
 * @since 2021-03-31
 */
@Api(description="小节管理")
@RestController
@RequestMapping("/introService/section")
@CrossOrigin
public class SectionController {

    //注入service
    @Autowired
    private SectionService sectionService;

    //添加章节  教师端和学生端暂时用不到，只用作快速向数据库插入数据
    @ApiOperation(value = "添加小节")
    @PostMapping("/addSection")
    public R addChapter(@RequestBody Section section){
        boolean save = sectionService.save(section);
        return save?R.ok():R.error();
    }
}

