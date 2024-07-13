package com.example.service;

import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Course;
import com.example.mapper.CourseMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 学院信息表业务处理
 **/
@Service
public class CourseService {

    @Resource
    private CourseMapper courseMapper;

    /**
     * 新增
     */
    public void add(Course course) {
        courseMapper.insert(course);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        courseMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            courseMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Course course) {
        courseMapper.updateById(course);
    }

    /**
     * 根据ID查询
     */
    public Course selectById(Integer id) {
        return courseMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Course> selectAll(Course course) {
        return courseMapper.selectAll(course);
    }

    /**
     * 分页查询
     */
    public PageInfo<Course> selectPage(Course course, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        //在这里设置如果登录用户是教师，就添加teacherId这个查询条件，即只查当前教师任教的课
        Account currentUser= TokenUtils.getCurrentUser();
        if(RoleEnum.TEACHER.name().equals(currentUser.getRole())){
            course.setTeacherId(currentUser.getId());
        }

        //如果不给course设属性，那默认就是查询课程表的所有信息
        //这里设置的查询属性其实是对应xml文件里的定义，selectAll方法下设置的属性就是查询属性（where 属性 = 属性值）
        List<Course> list = courseMapper.selectAll(course);
        return PageInfo.of(list);
    }

}