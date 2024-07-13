package com.example.entity;

/**
 * 评教类
 */
public class Comment {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Integer id;
    private String name; //课程名称
    private String teacher; //授课老师
    private String student; //评教学生
    private String content; //评教内容
    private String time; //评教时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
