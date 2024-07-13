package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.GpaAndRank;
import com.example.entity.Student;
import com.example.entity.StudentGpaDto;
import com.example.exception.CustomException;
import com.example.mapper.StudentMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 教师业务处理
 **/
@Service
public class StudentService {

    @Resource
    private StudentMapper studentMapper;

    /**
     * 新增
     */
    public void add(Student student) {
        //先从数据库表中查询有没有对应名字的教师，如果不是空，抛出异常，已经有了不能再加
        Student dbStudent = studentMapper.selectByUsername(student.getUsername());
        if (ObjectUtil.isNotNull(dbStudent)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        //如果要新增的教师的密码或用户名是空的，设置一个默认值
        if (ObjectUtil.isEmpty(student.getPassword())) {
            student.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(student.getName())) {
            student.setName(student.getUsername());
        }
        //给这个学生设定角色定位STUDENT （这个枚举在common-enums-RoleEnum里）
        student.setRole(RoleEnum.STUDENT.name());
        //再调用studentMapper中的方法将新增信息添加进数据库中
        studentMapper.insert(student);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        studentMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            studentMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Student student) {
        studentMapper.updateById(student);
    }

    /**
     * 根据ID查询
     */
    public Student selectById(Integer id) {
        return studentMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Student> selectAll(Student student) {
        return studentMapper.selectAll(student);
    }

    /**
     * 分页查询
     */
    public PageInfo<Student> selectPage(Student student, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Student> list = studentMapper.selectAll(student);
        return PageInfo.of(list);
    }

//    /**
//     * 登录
//     */
public Account login(Account account) {
    Account dbStudent = studentMapper.selectByUsername(account.getUsername());
    if (ObjectUtil.isNull(dbStudent)) {
        throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
    }
    if (!account.getPassword().equals(dbStudent.getPassword())) {
        throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
    }
    // 生成token
    String tokenData = dbStudent.getId() + "-" + RoleEnum.STUDENT.name();
    String token = TokenUtils.createToken(tokenData, dbStudent.getPassword());
    dbStudent.setToken(token);
    return dbStudent;
}
//
//    /**
//     * 注册
//     */
    public void register(Account account) {
        Student student = new Student();
        BeanUtils.copyProperties(account, student);
        add(student);
    }
//
//    /**
//     * 修改密码
//     */
    public void updatePassword(Account account) {
        Student dbStudent = studentMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbStudent)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbStudent.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbStudent.setPassword(account.getNewPassword());
        studentMapper.updateById(dbStudent);
    }

    public List<StudentGpaDto> getGpaRankingBySpeciality(Integer specialityId) {
        return studentMapper.getGpaRankingBySpeciality(specialityId);
    }

    public List<StudentGpaDto> getGpaRankingByClass(Integer classId) {
        return studentMapper.getGpaRankingByClass(classId);
    }

    /**
     * 获取学生的 GPA 和同专业内的排名
     */
    public GpaAndRank getGpaAndRank(Integer studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("Student not found");
        }

        // 获取同专业内的排名，假设同专业的学生按照 GPA 降序排列
        List<Student> studentsInSpeciality = studentMapper.selectBySpeciality(student.getSpecialityId());
        Integer rankInSpeciality = null;
        for (int i = 0; i < studentsInSpeciality.size(); i++) {
            if (studentsInSpeciality.get(i).getId().equals(studentId)) {
                rankInSpeciality = i + 1; // 排名从 1 开始
                break;
            }
        }

        // 返回封装好的 GPA 和排名信息
        return new GpaAndRank(student.getGpa(), rankInSpeciality);
    }

}