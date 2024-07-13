package com.example.mapper;

import com.example.entity.Grade;

import java.util.List;

/**
 * 操作grade相关数据接口
*/
public interface GradeMapper {

    /**
      * 新增
    */
    int insert(Grade grade);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Grade grade);

    /**
      * 根据ID查询
    */
    Grade selectById(Integer id);

    /**
      * 查询所有
    */
    List<Grade> selectAll(Grade grade);

}