package com.example.mapper;

import com.example.entity.Student;
import com.example.entity.StudentGpaDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作student相关数据接口
*/
public interface StudentMapper {

    /**
      * 新增
    */
    int insert(Student student);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Student student);

    /**
      * 根据ID查询
    */
    Student selectById(Integer id);

    /**
      * 查询所有
    */
    List<Student> selectAll(Student student);

    @Select("select * from student where username = #{username}")
    Student selectByUsername(String username);

    @Select("SELECT id FROM student WHERE class_id = #{classId}")
    List<Integer> selectStudentIdsByClassId(@Param("classId") Integer classId);

    @Select("SELECT s.name AS studentName, s.gpa " +
            "FROM student s " +
            "WHERE s.speciality_id = #{specialityId} " +
            "ORDER BY s.gpa DESC")
    List<StudentGpaDto> getGpaRankingBySpeciality(@Param("specialityId") Integer specialityId);

    @Select("SELECT s.name AS studentName, s.gpa " +
            "FROM student s " +
            "WHERE s.class_id = #{classId} " +
            "ORDER BY s.gpa DESC")
    List<StudentGpaDto> getGpaRankingByClass(@Param("classId") Integer classId);

    @Select("SELECT * FROM student WHERE speciality_id = #{specialityId} ORDER BY gpa DESC")
    List<Student> selectBySpeciality(@Param("specialityId") Integer specialityId);
}