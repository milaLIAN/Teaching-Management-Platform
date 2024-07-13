package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.*;
import com.example.entity.Score;
import com.example.service.ClassesService;
import com.example.service.ScoreService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.common.enums.RoleEnum;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公告信息表前端操作接口
 **/
@RestController
@RequestMapping("/score")
public class ScoreController {

    @Resource
    private ScoreService scoreService;

    @Resource
    private ClassesService classesService;
    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Score score) {
        scoreService.add(score);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        scoreService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        scoreService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
//    @PutMapping("/update")
//    public Result updateById(@RequestBody Score score) {
//        scoreService.updateById(score);
//        return Result.success();
//    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Score score = scoreService.selectById(id);
        return Result.success(score);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Score score ) {
        List<Score> list = scoreService.selectAll(score);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Score score,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Score> page = scoreService.selectPage(score, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 柱状图数据
     */
    @GetMapping("/getBar")
    public Result getBar(){
        Map<String,Object> resultMap=new HashMap<>(); //返回的是一个结果map key是title，subtitle，name，data，value可能是字符串或列表（data）

        List<String> xList = new ArrayList<>();
        List<Long> yList = new ArrayList<>(); //柱状图的x轴数据和y轴数据

        //处理数据
        List<Score>all=scoreService.selectAll(new Score()); //获取全部score表数据

        //优：90-100
        xList.add("优（90-100分）");
        yList.add(all.stream().filter(x -> x.getScore() >= 90).count()); //用filter筛选九十分以上的成绩个数
        //良：80-90
        xList.add("良（80分-89分）");
        yList.add(all.stream().filter(x -> x.getScore() >= 80&&x.getScore()<90).count());
        //中：70-80
        xList.add("中（70分-79分）");
        yList.add(all.stream().filter(x -> x.getScore() >= 70&&x.getScore()<80).count());
        //合格：60-70
        xList.add("及格（60分-69分）");
        yList.add(all.stream().filter(x -> x.getScore() >=  60&&x.getScore()<70).count());
        //不合格 <60
        xList.add("不及格（<60分）");
        yList.add(all.stream().filter(x -> x.getScore() <60).count());

        //绑定resultMap的数据
        resultMap.put("text","成绩统计图（柱状图）");
        resultMap.put("subtext","统计维度：成绩区间");
        resultMap.put("xAxis", xList);
        resultMap.put("yAxis", yList);

        return Result.success(resultMap);
    }

    @GetMapping("/average-by-class")
    public Result getAverageByClass(@RequestParam Integer courseId) {
        List<Classes> classes = classesService.selectAll(new Classes()); // 假设有一个 classService 可以获取所有班级信息
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Classes cls : classes) {
            // 获取某班级的所有学生ID列表
            List<Integer> studentIds = scoreService.selectStudentIdsByClassId(cls.getId());
            if (studentIds.isEmpty()) {
                continue;
            }
            // 根据学生ID列表和课程ID获取成绩记录
            List<Score> scores = scoreService.selectByStudentIdsAndCourseId(studentIds, courseId);
            if (scores.isEmpty()) {
                continue;
            }
            // 计算平均分
            double averageScore = scores.stream().collect(Collectors.averagingDouble(Score::getScore));
            // 将班级名称和平均分存入结果Map
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("class_name", cls.getName());
            resultMap.put("avg_score", averageScore);
            // 将结果Map加入结果列表
            resultList.add(resultMap);
        }
        // 返回结果列表
        return Result.success(resultList);
    }

    @GetMapping("/distribution-by-class")
    public Result distributionByClass(@RequestParam("courseId") Integer courseId) {
        List<Map<String, Object>> result = scoreService.getDistributionByClass(courseId);
        return Result.success(result);
    }

    @GetMapping("/distribution-by-student")
    public Result distributionByStudent() {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser.getRole().equals(RoleEnum.STUDENT.name())) {
            List<Map<String, Object>> result = scoreService.getDistributionByStudent(currentUser.getId());
            return Result.success(result);
        } else {
            return Result.error(ResultCodeEnum.STUDENT_ROLE_ERROR);
        }
    }

    @GetMapping("/distribution-by-teacher-course")
    public Result getDistributionByTeacherCourse(@RequestParam Integer courseId) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.TEACHER.name().equals(currentUser.getRole())) {
            List<ScoreDistribution> distribution = scoreService.getDistributionByTeacherCourse(currentUser.getId(), courseId);
            return Result.success(distribution);
        } else {
            return Result.error(ResultCodeEnum.TEACHER_ROLE_ERROR);
        }
    }

}