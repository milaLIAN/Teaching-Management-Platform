package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.*;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.example.common.enums.ResultCodeEnum;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公告信息表业务处理
 **/
@Service
public class ScoreService {

    @Resource
    private ScoreMapper scoreMapper;
    @Resource
    private StudentMapper studentMapper;
    @Resource
    private CourseMapper courseMapper;

    @Resource
    private WarnMapper warnMapper;

    /**
     * 新增
     */
    public void add(Score score) {
        //先判断是否已经录入过了该学生的成绩信息 如果已经录入过，则不需要在录入
        Score dbScore=scoreMapper.selectByCourseIdAndStudentId(score.getCourseId(),score.getStudentId());
        if (ObjectUtil.isNotEmpty(dbScore)) {
            throw new CustomException(ResultCodeEnum.SCORE_ALREADY_ERROR);
        }

        //再计算并插入总成绩
        double total = score.getOrdinaryScore() * 0.4 + score.getExamScore() * 0.6;
        score.setScore(total);
        scoreMapper.insert(score);

        // 录入之后，及格的学生需要获取对应的学分（修改student表的数据）
        if (total >= 60) {
            Course course = courseMapper.selectById(score.getCourseId());
            Student student = studentMapper.selectById(score.getStudentId()); //获取当前课程和学生
            student.setScore(student.getScore() + course.getScore()); //调用setScore方法
            studentMapper.updateById(student); //调用更新方法修改student表
        }else {
            // 如果成绩不及格，插入学业预警数据
            Warn warn = new Warn();
            Integer scoreId = scoreMapper.selectScoreIdByStudentIdAndCourseId(score.getStudentId(), score.getCourseId());
            warn.setStudentId(score.getStudentId());
            warn.setScoreId(scoreId);
            warn.setCourseId(score.getCourseId());
            warn.setContent("该课程成绩不及格，请注意！"); // 设置预警内容

            warnMapper.insert(warn);
        }
        // 计算并更新 GPA
        calculateAndUpdateGPA(score.getStudentId());
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {

        //删除成绩扣学分
        Score score=scoreMapper.selectById(id);
        Course course = courseMapper.selectById(score.getCourseId());
        Student student = studentMapper.selectById(score.getStudentId()); //获取当前课程和学生
        if(student.getScore()>0){//避免把学分扣成负数
            student.setScore(student.getScore() - course.getScore());
            studentMapper.updateById(student); //调用更新方法修改student表
        }
        // 删除对应的学业预警
        warnMapper.deleteByScoreId(id);

        scoreMapper.deleteById(id);
        // 计算并更新 GPA
        calculateAndUpdateGPA(score.getStudentId());
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            scoreMapper.deleteById(id);
        }
    }

    /**
     * 修改 成绩模块暂时没有编辑功能了
     */
//    public void updateById(Score score) {
//        double oldScore=score.getScore();
//        double newScore=0.6*score.getExamScore()+0.4* score.getOrdinaryScore();
//        score.setScore(newScore); //修改部分也要修改新总成绩
//        // 修改之后，不及格/及格的学生减去/加上获取对应的学分（修改student表的数据）
//        if (oldScore>=60&&newScore < 60) {
//            Course course = courseMapper.selectById(score.getCourseId());
//            Student student = studentMapper.selectById(score.getStudentId()); //获取当前课程和学生
//            student.setScore(student.getScore() - course.getScore()); //调用setScore方法
//            studentMapper.updateById(student); //调用更新方法修改student表
//        }
//        if (oldScore<60&&newScore >= 60){
//            Course course = courseMapper.selectById(score.getCourseId());
//            Student student = studentMapper.selectById(score.getStudentId()); //获取当前课程和学生
//            student.setScore(student.getScore() + course.getScore()); //调用setScore方法
//            studentMapper.updateById(student); //调用更新方法修改student表
//        }
//        scoreMapper.updateById(score);
//    }

    /**
     * 根据ID查询
     */
    public Score selectById(Integer id) {
        return scoreMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Score> selectAll(Score score) {
        return scoreMapper.selectAll(score);
    }

    /**
     * 分页查询
     */
    public PageInfo<Score> selectPage(Score score, Integer pageNum, Integer pageSize) {
        //教师和学生都只能看到自己（任教的课程）的成绩
        //这里要改成 教师能看到自己班级的学生的成绩
//        Account currentUser = TokenUtils.getCurrentUser();
//        if (RoleEnum.TEACHER.name().equals(currentUser.getRole())) {
//            score.setTeacherId(currentUser.getId());
//        }
//        if (RoleEnum.STUDENT.name().equals(currentUser.getRole())) {
//            score.setStudentId(currentUser.getId());
//        }
//        PageHelper.startPage(pageNum, pageSize);
//        List<Score> list = scoreMapper.selectAll(score);
//        return PageInfo.of(list);
        Account currentUser = TokenUtils.getCurrentUser();
        PageHelper.startPage(pageNum, pageSize);
        List<Score> list;

        if (RoleEnum.TEACHER.name().equals(currentUser.getRole())) {
            System.out.println("courseId: " + score.getCourseId());
            list = scoreMapper.selectByTeacherId(currentUser.getId(),score.getCourseId());
        } else if (RoleEnum.STUDENT.name().equals(currentUser.getRole())) {
            score.setStudentId(currentUser.getId());
            list = scoreMapper.selectAll(score);
        } else {
            // 其他角色的处理逻辑，如管理员可以查看所有成绩
            list = scoreMapper.selectAll(score);
        }

        return PageInfo.of(list);
    }

    public List<Score> selectByStudentIdsAndCourseId(List<Integer> studentIds, Integer courseId) {
        return scoreMapper.selectByStudentIdsAndCourseId(studentIds, courseId);
    }

    public List<Integer> selectStudentIdsByClassId(Integer classId) {
        return studentMapper.selectStudentIdsByClassId(classId);
    }
    public List<Map<String, Object>> getDistributionByClass(Integer courseId) {
        return scoreMapper.getDistributionByClass(courseId);
    }

    public List<Map<String, Object>> getDistributionByStudent(Integer studentId) {
        return scoreMapper.getDistributionByStudent(studentId);
    }

    public List<ScoreDistribution> getDistributionByTeacherCourse(Integer teacherId, Integer courseId) {
        return scoreMapper.selectDistributionByTeacherCourse(teacherId, courseId);
    }

    public void calculateAndUpdateGPA(Integer studentId) {
        // 获取该学生的所有成绩
        List<Score> scores = scoreMapper.selectByStudentId(studentId);

        // 获取所有相关课程
        List<Integer> courseIds = scores.stream().map(Score::getCourseId).collect(Collectors.toList());
        List<Course> courses = courseMapper.selectByIds(courseIds);
        Map<Integer, Course> courseMap = courses.stream().collect(Collectors.toMap(Course::getId, course -> course));

        // 计算加权 GPA
        double gpa = GPAUtil.calculateWeightedGPA(scores, courseMap);

        // 输出计算的 GPA
        System.out.println("Calculating GPA for studentId: " + studentId);
        System.out.println("Calculated GPA: " + gpa);

        // 更新学生的 GPA
        Student student=studentMapper.selectById(studentId);
        student.setGpa(gpa);
        studentMapper.updateById(student);
    }

}