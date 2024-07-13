package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Account;
import com.example.entity.Grade;
import com.example.mapper.GradeMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公告信息表业务处理
 **/
@Service
public class GradeService {

    @Resource
    private GradeMapper gradeMapper;

    /**
     * 新增
     */
    public void add(Grade grade) {
        gradeMapper.insert(grade);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        gradeMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            gradeMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Grade grade) {
        gradeMapper.updateById(grade);
    }

    /**
     * 根据ID查询
     */
    public Grade selectById(Integer id) {
        return gradeMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Grade> selectAll(Grade grade) {
        return gradeMapper.selectAll(grade);
    }

    /**
     * 分页查询
     */
    public PageInfo<Grade> selectPage(Grade grade, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Grade> list = gradeMapper.selectAll(grade);
        return PageInfo.of(list);
    }

}