package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Course;
import com.example.entity.Homework;
import com.example.mapper.CourseMapper;
import com.example.mapper.HomeworkMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 作业信息表业务处理
 **/
@Service
public class HomeworkService {

    @Resource
    private HomeworkMapper homeworkMapper;
    @Resource
    private CourseMapper courseMapper;

    /**
     * 新增
     */
    public void add(Homework homework) {
        Course course=courseMapper.selectById(homework.getCourseId());
        homework.setTeacherId(course.getTeacherId());
        homeworkMapper.insert(homework);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        homeworkMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            homeworkMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Homework homework) {
        homeworkMapper.updateById(homework);
    }

    /**
     * 根据ID查询
     */
    public Homework selectById(Integer id) {
        return homeworkMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Homework> selectAll(Homework homework) {
        return homeworkMapper.selectAll(homework);
    }

    /**
     * 分页查询
     */
    public PageInfo<Homework> selectPage(Homework homework, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        //学生只能看到自己的作业 老师也只能看到自己任课课程的作业
        Account currentUser= TokenUtils.getCurrentUser();
        if(RoleEnum.STUDENT.name().equals(currentUser.getRole())){
            homework.setStudentId(currentUser.getId());
        }
        if(RoleEnum.TEACHER.name().equals(currentUser.getRole())){
            homework.setTeacherId(currentUser.getId());
        }

        List<Homework> list = homeworkMapper.selectAll(homework);
        return PageInfo.of(list);
    }

}