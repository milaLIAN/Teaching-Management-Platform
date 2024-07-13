package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Warn;
import com.example.mapper.WarnMapper;
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
public class WarnService {

    @Resource
    private WarnMapper warnMapper;

    /**
     * 新增
     */
    public void add(Warn warn) {
        warnMapper.insert(warn);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        warnMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            warnMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Warn warn) {
        warnMapper.updateById(warn);
    }

    /**
     * 根据ID查询
     */
    public Warn selectById(Integer id) {
        return warnMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Warn> selectAll(Warn warn) {
        return warnMapper.selectAll(warn);
    }

    /**
     * 分页查询
     */
    public PageInfo<Warn> selectPage(Warn warn, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Account currentUser = TokenUtils.getCurrentUser();
        List<Warn> list;

        if (RoleEnum.TEACHER.name().equals(currentUser.getRole())) {
            System.out.println("Teacher ID: " + currentUser.getId());
            list = warnMapper.selectByTeacherId(currentUser.getId());
        } else if (RoleEnum.STUDENT.name().equals(currentUser.getRole())) {
            warn.setStudentId(currentUser.getId());
            list = warnMapper.selectAll(warn);
        } else {
            // 其他角色的处理逻辑，如管理员可以查看所有学业预警
            list = warnMapper.selectAll(warn);
        }

        return PageInfo.of(list);
    }

}