package com.introtoc.introService.utils;

import com.introtoc.introService.entity.Topic;
import com.introtoc.introService.service.TopicService;
import com.introtoc.serviceBase.exceptionHandler.IntroException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StringHandleUtils {

//    @Autowired //注意static不能和@Autowired一起使用
    private static TopicService topicService;

    @Autowired
    public StringHandleUtils(TopicService topicService) {
        StringHandleUtils.topicService = topicService;
    }

    //辅助函数 将多选的答案按照答案号返回
    public static String findCheckValue(String answers) {
        String regex = "[A-Z]\\.";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(answers);
        StringBuilder sb = new StringBuilder();
        while(m.find()){
            sb.append(m.group(), 0, 1);
        }
        return sb.toString();
    }

    //辅助函数 分割前端传过来的答案

    //辅助函数，将练习里得到的答案和标准答案进行批改评分
    public static int checkAnswers(String[] topicIds, String[] ans) {
        int passTopic = 0;
        int index = 0;
        //访问题目集进行答案验证
        System.out.println(Arrays.toString(topicIds));
        for (String topicId : topicIds) {
            Topic topic = topicService.getById(topicId);
            System.out.println(topic.toString());
            if(topic.getType()==0 && topic.getAnswer()!=null && topic.getAnswer().equals(ans[index].substring(0,1))) { //分别验证 0->单选题
                System.out.println("单选答案验证：" + topic.getAnswer() + "_______" + ans[index]);
                passTopic++;
            }else if(topic.getType()==1){
                //TODO 多选题将答案提取出来
                ans[index] = findCheckValue(ans[index]);
                System.out.println("多选答案验证：" + topic.getAnswer() + "_______" + ans[index]);
                if(topic.getAnswer().equals(ans[index])){
                    passTopic++;
                }
            }else{//简答题
                if(topic.getAnswer().equals(ans[index])){
                    System.out.println("简答题答案验证：" + topic.getAnswer() + "_______" + ans[index]);
                    passTopic++;
                }
            }
            index++;
        }
        return (passTopic * 100 / topicIds.length);
    }


    public static String[] splitAnswer(String stuAnswers, int topicIdLength) {
        String[] ans = stuAnswers.split("_ans_");
        System.out.println("分割后的答案数组："+ Arrays.toString(ans));
        if(ans.length!=topicIdLength){ //如果传到后端的答案数量和题目数量不一致 抛异常
            throw new IntroException(20001,"答案数量与题目数量不一致");
        }
        return ans;
    }
}
