package com.introtoc.introService.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

//创建跟excel对应的student实体类
@Data
public class StudentDate {
    /**student excel表格式
     * stuNum name classes grade
     *
     */
    @ExcelProperty(index = 0)
    private String stuNum;

    @ExcelProperty(index = 1)
    private String name;

    @ExcelProperty(index = 2)
    private String classes;

    @ExcelProperty(index = 3)
    private String grade;

    //5-19新添加 有问题删除
    @ExcelProperty(index = 4)
    private String groupId;

}
