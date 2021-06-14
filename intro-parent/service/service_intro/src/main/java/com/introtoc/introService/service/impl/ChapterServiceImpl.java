package com.introtoc.introService.service.impl;

import com.introtoc.introService.entity.Chapter;
import com.introtoc.introService.entity.Knowledge;
import com.introtoc.introService.entity.Section;
import com.introtoc.introService.entity.chapter.OneChapter;
import com.introtoc.introService.entity.chapter.ThreeChapter;
import com.introtoc.introService.entity.chapter.TwoChapter;
import com.introtoc.introService.mapper.ChapterMapper;
import com.introtoc.introService.service.ChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.introtoc.introService.service.KnowledgeService;
import com.introtoc.introService.service.SectionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tengsss
 * @since 2021-03-31
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    //引入小节的处理类
    @Autowired
    private SectionService sectionService;
    @Autowired
    private KnowledgeService knowledgeService;

    //获取所有章节信息 【知识点】目录 将所有小节信息封装到章节中
    @Override
    public List<OneChapter> getAllOneAndTwoChapter() {
        //查询所有一级目录
        List<Chapter> oneChapterList = baseMapper.selectList(null);

        //查询所有二级目录
        List<Section> twoChapterList = sectionService.list(null);

        //查询所有三级目录
        List<Knowledge> knowledgeList = knowledgeService.list(null);

        //封装最终链表集合
        List<OneChapter> finalChapterList = new ArrayList<>();

        //遍历一级和二级进行封装
        //查询出来所有的一级目录List集合遍历，得到每一个一级分类对象，获取每个一级对象的值
        //封装到要求的list集合里面 List<OneSubject> finalSubjectList
        for(int i=0;i<oneChapterList.size();i++){
            Chapter chapter = oneChapterList.get(i);
            //把chapter的值取出来放到oneChapter对象里面
            OneChapter oneChapter = new OneChapter();
            //通过工具类复制 要求属性名称一致
            BeanUtils.copyProperties(chapter,oneChapter);

            //封装二级目录
            for(int j=0;j<twoChapterList.size();j++){
                Section section = twoChapterList.get(j);
                //获取二级对象父id 并判断
                //在一级对象中添加该二级对象
                if(section.getChapterId().equals(oneChapter.getId())){
                    TwoChapter twoChapter = new TwoChapter();
                    BeanUtils.copyProperties(section,twoChapter);
                    for(int k=0;k<knowledgeList.size();k++){
                        Knowledge knowledge = knowledgeList.get(k);
                        //获取三级对象父id 判断
                        //在二级对象中添加该三级对象
                        if(knowledge.getChapterId().equals(oneChapter.getId()) && knowledge.getSectionId().equals(twoChapter.getId())){
                            ThreeChapter threeChapter = new ThreeChapter();
                            BeanUtils.copyProperties(knowledge,threeChapter);
                            //将三级分类放到二级分类中
                            twoChapter.getChildren().add(threeChapter);
                        }
                    }

                    //将二级分类放到一级分类的子中
                    oneChapter.getChildren().add(twoChapter);
                }
            }
            //把多个OneSubject放到finalSubjectList
            finalChapterList.add(oneChapter);

        }
        return finalChapterList;
    }
}
