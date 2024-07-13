package com.example.mapper;

import com.example.entity.Course;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作course相关数据接口
*/
public interface CourseMapper {

    /**
      * 新增
    */
    int insert(Course course);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Course course);

    /**
      * 根据ID查询
    */
    Course selectById(Integer id);

    /**
      * 查询所有
    */
    List<Course> selectAll(Course course);

    @Select({
            "<script>",
            "SELECT * FROM course",
            "WHERE id IN",
            "<if test='ids != null and !ids.isEmpty()'>",
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</if>",
            "<if test='ids == null or ids.isEmpty()'>",
            "(NULL)",  // 使用 (NULL) 作为不会匹配任何记录的占位符
            "</if>",
            "</script>"
    })
    List<Course> selectByIds(@Param("ids") List<Integer> ids);

}