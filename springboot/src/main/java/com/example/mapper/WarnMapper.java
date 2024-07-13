package com.example.mapper;

import com.example.entity.Warn;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作warn相关数据接口
*/
public interface WarnMapper {

    /**
      * 新增
    */
    int insert(Warn warn);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Warn warn);

    /**
      * 根据ID查询
    */
    Warn selectById(Integer id);

    /**
      * 查询所有
    */
    List<Warn> selectAll(Warn warn);

    @Delete("DELETE FROM warn WHERE score_id = #{scoreId}")
    void deleteByScoreId(@Param("scoreId") Integer scoreId);

    @Select("SELECT w.*, st.name as studentName, sc.score as scoreValue, co.name as courseName " +
            "FROM warn w " +
            "JOIN student st ON w.student_id = st.id " +
            "JOIN classes c ON st.class_id = c.id " +
            "JOIN teacher t ON c.teacher_id = t.id " +
            "JOIN score sc ON w.score_id = sc.id " +
            "JOIN course co ON sc.course_id = co.id " +
            "WHERE t.id = #{teacherId}")
    List<Warn> selectByTeacherId(@Param("teacherId") Integer teacherId);

}