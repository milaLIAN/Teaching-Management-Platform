package com.example.mapper;

import com.example.entity.Classes;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作classes相关数据接口
*/
public interface ClassesMapper {

    /**
      * 新增
    */
    int insert(Classes classes);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Classes classes);

    /**
      * 根据ID查询
    */
    Classes selectById(Integer id);

    /**
      * 查询所有
    */
    List<Classes> selectAll(Classes classes);

    @Select("SELECT id, name FROM classes WHERE speciality_id = #{specialityId}")
    List<Classes> selectBySpeciality(@Param("specialityId") Integer specialityId);

    @Select("SELECT id FROM classes WHERE teacher_id = #{teacherId}")
    Classes selectByTeacher(@Param("teacherId") Integer teacherId);
}