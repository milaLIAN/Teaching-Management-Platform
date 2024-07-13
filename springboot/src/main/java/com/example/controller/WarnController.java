package com.example.controller;

import com.example.common.Result;
import com.example.entity.Warn;
import com.example.service.WarnService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公告信息表前端操作接口
 **/
@RestController
@RequestMapping("/warn")
public class WarnController {

    @Resource
    private WarnService warnService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Warn warn) {
        warnService.add(warn);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        warnService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        warnService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Warn warn) {
        warnService.updateById(warn);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Warn warn = warnService.selectById(id);
        return Result.success(warn);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Warn warn ) {
        List<Warn> list = warnService.selectAll(warn);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Warn warn,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Warn> page = warnService.selectPage(warn, pageNum, pageSize);
        return Result.success(page);
    }

}