package com.introtoc.introService.entity.chapter;

import com.introtoc.introService.entity.Knowledge;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

//二级目录 小节目录
@Data
public class TwoChapter {

    private String id;
    private String title;

    //一个二级分类有多个三级分类Knowledge
    private List<ThreeChapter> children = new ArrayList<>();
}
