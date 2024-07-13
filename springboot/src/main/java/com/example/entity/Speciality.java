package com.example.entity;

/**
 * 学院信息表实体
 */
public class Speciality {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name; //专业名称
    private String content; //专业介绍
    private Integer collegeId; //所属学院id
    private Integer score; //要求学分

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Integer collegeId) {
        this.collegeId = collegeId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    private String collegeName; //所属学院名称

}
