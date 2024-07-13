package com.example.mapper;

import com.example.entity.Score;
import com.example.entity.ScoreDistribution;
import com.example.entity.ScoreDistributionDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 操作score相关数据接口
*/
public interface ScoreMapper {

    /**
      * 新增
    */

    int insert(Score score);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Score score);

    /**
      * 根据ID查询
    */
    Score selectById(Integer id);

    /**
      * 查询所有
    */
    List<Score> selectAll(Score score);

    @Select("SELECT * FROM score WHERE student_id = #{studentId}")
    List<Score> selectByStudentId(Integer studentId);

    @Select("select * from score where student_id=#{studentId} and course_id=#{courseId}")
    Score selectByCourseIdAndStudentId(@Param("courseId") Integer courseId, @Param("studentId") Integer studentId);

    @Select({
            "<script>",
            "SELECT s.*, st.name as studentName, t.name as teacherName, co.name as courseName ",
            "FROM score s ",
            "JOIN student st ON s.student_id = st.id ",
            "JOIN classes c ON st.class_id = c.id ",
            "JOIN course co ON co.id = s.course_id ",
            "JOIN teacher t ON t.id = #{teacherId} ",
            "WHERE c.teacher_id = #{teacherId} ",
            "<if test='courseId != null'>",
            "AND co.id = #{courseId} ",
            "</if>",
            "</script>"
    })
    List<Score> selectByTeacherId(@Param("teacherId") Integer teacherId, @Param("courseId") Integer courseId);

    @Select("SELECT id FROM score WHERE student_id = #{studentId} AND course_id = #{courseId}")
    Integer selectScoreIdByStudentIdAndCourseId(@Param("studentId") Integer studentId, @Param("courseId") Integer courseId);

    @Select("<script>" +
            "SELECT * FROM score " +
            "WHERE course_id = #{courseId} " +
            "AND student_id IN " +
            "<foreach item='studentId' collection='studentIds' open='(' separator=',' close=')'>" +
            "#{studentId}" +
            "</foreach>" +
            "</script>")
    List<Score> selectByStudentIdsAndCourseId(@Param("studentIds") List<Integer> studentIds, @Param("courseId") Integer courseId);

    @Select("SELECT s.class_id AS classId, c.name AS className, " +
            "SUM(CASE WHEN sc.score >= 90 THEN 1 ELSE 0 END) AS excellent, " +
            "SUM(CASE WHEN sc.score >= 80 AND sc.score < 90 THEN 1 ELSE 0 END) AS good, " +
            "SUM(CASE WHEN sc.score >= 70 AND sc.score < 80 THEN 1 ELSE 0 END) AS average, " +
            "SUM(CASE WHEN sc.score >= 60 AND sc.score < 70 THEN 1 ELSE 0 END) AS pass, " +
            "SUM(CASE WHEN sc.score < 60 THEN 1 ELSE 0 END) AS fail " +
            "FROM score sc " +
            "JOIN student s ON sc.student_id = s.id " +
            "JOIN classes c ON s.class_id = c.id " +
            "WHERE sc.course_id = #{courseId} " +
            "GROUP BY s.class_id, c.name")
    List<Map<String, Object>> getDistributionByClass(@Param("courseId") Integer courseId);

    @Select("SELECT " +
            "SUM(CASE WHEN score >= 90 THEN 1 ELSE 0 END) as excellent, " +
            "SUM(CASE WHEN score >= 80 AND score < 90 THEN 1 ELSE 0 END) as good, " +
            "SUM(CASE WHEN score >= 70 AND score < 80 THEN 1 ELSE 0 END) as average, " +
            "SUM(CASE WHEN score >= 60 AND score < 70 THEN 1 ELSE 0 END) as pass, " +
            "SUM(CASE WHEN score < 60 THEN 1 ELSE 0 END) as fail " +
            "FROM score WHERE student_id = #{studentId}")
    List<Map<String, Object>> getDistributionByStudent(Integer studentId);

    @Select("SELECT " +
            "SUM(CASE WHEN s.score >= 90 THEN 1 ELSE 0 END) AS excellent, " +
            "SUM(CASE WHEN s.score >= 80 AND s.score < 90 THEN 1 ELSE 0 END) AS good, " +
            "SUM(CASE WHEN s.score >= 70 AND s.score < 80 THEN 1 ELSE 0 END) AS average, " +
            "SUM(CASE WHEN s.score >= 60 AND s.score < 70 THEN 1 ELSE 0 END) AS pass, " +
            "SUM(CASE WHEN s.score < 60 THEN 1 ELSE 0 END) AS fail " +
            "FROM score s " +
            "JOIN student st ON s.student_id = st.id " +
            "JOIN classes c ON st.class_id = c.id " +
            "JOIN course co ON co.id = s.course_id " +
            "WHERE co.id = #{courseId} AND c.teacher_id = #{teacherId}")
    List<ScoreDistribution> selectDistributionByTeacherCourse(@Param("teacherId") Integer teacherId, @Param("courseId") Integer courseId);

}