package com.example.controller;

import com.example.common.Result;
import com.example.entity.Grade;
import com.example.service.GradeService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公告信息表前端操作接口
 **/
@RestController
@RequestMapping("/grade")
public class GradeController {

    @Resource
    private GradeService gradeService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Grade grade) {
        gradeService.add(grade);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        gradeService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        gradeService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Grade grade) {
        gradeService.updateById(grade);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Grade grade = gradeService.selectById(id);
        return Result.success(grade);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Grade grade ) {
        List<Grade> list = gradeService.selectAll(grade);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Grade grade,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Grade> page = gradeService.selectPage(grade, pageNum, pageSize);
        return Result.success(page);
    }

}