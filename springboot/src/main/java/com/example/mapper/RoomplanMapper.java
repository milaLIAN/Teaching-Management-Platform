package com.example.mapper;

import com.example.entity.Roomplan;

import java.util.List;

/**
 * 教室安排相关数据接口
*/
public interface RoomplanMapper {

    /**
     * 新增
     */
    int insert(Roomplan roomplan);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Roomplan roomplan);

    /**
     * 根据ID查询
     */
    Roomplan selectById(Integer id);

    /**
     * 查询所有
     */
    List<Roomplan> selectAll(Roomplan roomplan);

}