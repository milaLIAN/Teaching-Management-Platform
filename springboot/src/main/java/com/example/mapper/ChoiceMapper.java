package com.example.mapper;

import com.example.entity.Choice;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作choice相关数据接口
*/
public interface ChoiceMapper {

    /**
      * 新增
    */
    int insert(Choice choice);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Choice choice);

    /**
      * 根据ID查询
    */
    Choice selectById(Integer id);

    /**
      * 查询所有
    */
    List<Choice> selectAll(Choice choice);

    //其实我感觉根本不需要下面两个啊。。 直接在Service里new一个Choice，然后调用setCourseId/setStudentId，再调用selectAll就行了。。课表部分不也是这样。。
    //所以我感觉所有的条件查询都可以用selectAll实现，在xml文件下面添加要查询的属性，然后在Service里给对象用set方法设置属性就行了......
    @Select("select * from choice where course_id = #{courseId}")
    List<Choice> selectByCourseId(Integer courseId);

    @Select("select * from choice where student_id = #{studentId}")
    List<Choice> selectByStudentId(Integer studentId);
}