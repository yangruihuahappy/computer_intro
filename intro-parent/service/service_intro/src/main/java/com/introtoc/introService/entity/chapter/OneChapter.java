package com.introtoc.introService.entity.chapter;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

//一级目录 章节
@Data
public class OneChapter {
    private String id;
    private String title;

    //一个一级分类有多个二级分类
    private List<TwoChapter> children = new ArrayList<>();
}
