package com.example.service;

import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.common.enums.SegmentEnum;
import com.example.common.enums.WeekEnum;
import com.example.entity.Account;
import com.example.entity.Choice;
import com.example.entity.Curriculum;
import com.example.entity.Course;
import com.example.exception.CustomException;
import com.example.mapper.ChoiceMapper;
import com.example.mapper.CourseMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 学院信息表业务处理
 **/
@Service
public class ChoiceService {

    @Resource
    private ChoiceMapper choiceMapper;

    @Resource
    private CourseMapper courseMapper;

    /**
     * 新增
     */
    public void add(Choice choice) {
        //获取当前选的课
        Course course=courseMapper.selectById(choice.getCourseId());
        //①判断课程是否选择人数已满
        List<Choice>list=choiceMapper.selectByCourseId(choice.getCourseId());
        if(course.getNum().equals(list.size())){
            throw new CustomException(ResultCodeEnum.COURSE_NUM_ERROR);
        }
        //②判断该学生选这门课的时间与之前的选课是否有冲突
        List<Choice> slist=choiceMapper.selectByStudentId(choice.getStudentId());
        for(Choice dbChoice:slist){ // 对之前选过的每一门课进行判断
            Course tempcourse=courseMapper.selectById(dbChoice.getCourseId());
            if(course.getWeek().equals(tempcourse.getWeek())&&course.getSegment().equals(tempcourse.getSegment())){
                throw new CustomException("-1","你之前已选过"+tempcourse.getName()+" 与这门课的上课时间冲突，请重新选择");
            }
        }
        //如果都没有冲突，才执行插入操作
        choiceMapper.insert(choice);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        choiceMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            choiceMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Choice choice) {
        choiceMapper.updateById(choice);
    }

    /**
     * 根据ID查询
     */
    public Choice selectById(Integer id) {
        return choiceMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Choice> selectAll(Choice choice) {
        return choiceMapper.selectAll(choice);
    }

    /**
     * 分页查询
     */
    public PageInfo<Choice> selectPage(Choice choice, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        //在这里设置如果登录用户是教师，就添加teacherId这个查询条件，即只查当前教师任教的课
        Account currentUser= TokenUtils.getCurrentUser();
        if(RoleEnum.TEACHER.name().equals(currentUser.getRole())){
            choice.setTeacherId(currentUser.getId());
        }
        //教师只能看到自己任教的选课
        if(RoleEnum.STUDENT.name().equals(currentUser.getRole())){
            choice.setStudentId(currentUser.getId());
        }
        //这里新增对学生的判断，学生也只能看到自己的选课
        List<Choice> list = choiceMapper.selectAll(choice);
        return PageInfo.of(list);
    }

    /**
     * 生成课表
     */
    public List<Curriculum> getCurriculum() {
        Account currentUser = TokenUtils.getCurrentUser();
        // 查询出当前学生用户所有的选课信息
        Choice choice = new Choice();
        choice.setStudentId(currentUser.getId());
        List<Choice> choiceList = choiceMapper.selectAll(choice);

        List<Curriculum> list = new ArrayList<>();
        // 按照第几大节和周几来处理数据（总共有5大节，8列数据）
        // 第一大节（08:00 ~ 09:30） （课表的第一行数据）
        Curriculum first = new Curriculum(); //每个curriculum对象代表课表的一行，因为它有周一到周日七个属性，
        first.setSegment(SegmentEnum.FIRST.segment); //将第一行的时间属性（第一列）设为“第一大节（8:00-9:40）后面的也是一样
        processWeek(first, getWeekChoiceList(choiceList, SegmentEnum.FIRST.segment)); //处理第一行第2-8列的数据（一周内的排课）
        list.add(first); //将第一行加入总的课表列表

        // 第二大节（09:40 ~ 12:00）
        Curriculum second = new Curriculum();
        second.setSegment(SegmentEnum.SECOND.segment);
        processWeek(second, getWeekChoiceList(choiceList, SegmentEnum.SECOND.segment));
        list.add(second);

        // 第三大节（14:00 ~ 15:30）
        Curriculum third = new Curriculum();
        third.setSegment(SegmentEnum.THIRD.segment);
        processWeek(third, getWeekChoiceList(choiceList, SegmentEnum.THIRD.segment));
        list.add(third);

        // 第四大节（15:40 ~ 17:00）
        Curriculum forth = new Curriculum();
        forth.setSegment(SegmentEnum.FORTH.segment);
        processWeek(forth, getWeekChoiceList(choiceList, SegmentEnum.FORTH.segment));
        list.add(forth);

        // 第五大节（18:00 ~ 20:00）
        Curriculum fifth = new Curriculum();
        fifth.setSegment(SegmentEnum.FIFTH.segment);
        processWeek(fifth, getWeekChoiceList(choiceList, SegmentEnum.FIFTH.segment));
        list.add(fifth);

        return list;
    }
    /*筛选第n大节的课程*/
    private List<Choice> getWeekChoiceList(List<Choice> choiceList,String segment){
        return choiceList.stream().filter(x->x.getSegment().equals(segment)).collect(Collectors.toList());
        //筛选出当前学生选课中 属于第n大节的课程
    }

    /*
    * 处理周一到周日的数据
    * */
    private void processWeek(Curriculum curriculum,List<Choice> choiceList){
        //周一 再进行一次筛选，这次筛选的属性是周几
        Optional<Choice> first=choiceList.stream().filter(x->x.getWeek().equals(WeekEnum.MONDAY.week)).findFirst();
        //因为传入的choiceList本来就是第n大节的选课信息，因为我们处理过选课冲突，所以每大节在同一天内只会有一节课，所以用findFirst取一条数据即可
        first.ifPresent(choice -> curriculum.setMonday(choice.getName() + " (" + choice.getTeacherName() + ")"));
        //将当前传入的curriculum的Monday属性值设为在第n大节周一的课 的课程名和任课老师

        //剩下周二到周日的处理思路也是一样
        // 周二
        Optional<Choice> second = choiceList.stream().filter(x -> x.getWeek().equals(WeekEnum.TUESDAY.week)).findFirst();
        second.ifPresent(choice -> curriculum.setTuesday(choice.getName() + " (" + choice.getTeacherName() + ")"));
        // 周三
        Optional<Choice> third = choiceList.stream().filter(x -> x.getWeek().equals(WeekEnum.WEDNESDAY.week)).findFirst();
        third.ifPresent(choice -> curriculum.setWednesday(choice.getName() + " (" + choice.getTeacherName() + ")"));
        // 周四
        Optional<Choice> forth = choiceList.stream().filter(x -> x.getWeek().equals(WeekEnum.THURSDAY.week)).findFirst();
        forth.ifPresent(choice -> curriculum.setThursday(choice.getName() + " (" + choice.getTeacherName() + ")"));
        // 周五
        Optional<Choice> fifth = choiceList.stream().filter(x -> x.getWeek().equals(WeekEnum.FRIDAY.week)).findFirst();
        fifth.ifPresent(choice -> curriculum.setFriday(choice.getName() + " (" + choice.getTeacherName() + ")"));
        // 周六
        Optional<Choice> sixth = choiceList.stream().filter(x -> x.getWeek().equals(WeekEnum.SATURDAY.week)).findFirst();
        sixth.ifPresent(choice -> curriculum.setSaturday(choice.getName() + " (" + choice.getTeacherName() + ")"));
        // 周日
        Optional<Choice> seventh = choiceList.stream().filter(x -> x.getWeek().equals(WeekEnum.SUNDAY.week)).findFirst();
        seventh.ifPresent(choice -> curriculum.setSunday(choice.getName() + " (" + choice.getTeacherName() + ")"));

    }
}