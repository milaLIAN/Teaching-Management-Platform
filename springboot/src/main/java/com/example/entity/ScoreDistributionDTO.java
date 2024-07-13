package com.example.entity;

public class ScoreDistributionDTO {
    private static final long serialVersionUID = 1L;
    private Integer classId;
    private Integer excellent;
    private Integer good;
    private Integer average;
    private Integer pass;
    private Integer fail;

    // 构造函数、Getter 和 Setter 方法
    public ScoreDistributionDTO(Integer classId, Integer excellent, Integer good, Integer average, Integer pass, Integer fail) {
        this.classId = classId;
        this.excellent = excellent;
        this.good = good;
        this.average = average;
        this.pass = pass;
        this.fail = fail;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getExcellent() {
        return excellent;
    }

    public void setExcellent(Integer excellent) {
        this.excellent = excellent;
    }

    public Integer getGood() {
        return good;
    }

    public void setGood(Integer good) {
        this.good = good;
    }

    public Integer getAverage() {
        return average;
    }

    public void setAverage(Integer average) {
        this.average = average;
    }

    public Integer getPass() {
        return pass;
    }

    public void setPass(Integer pass) {
        this.pass = pass;
    }

    public Integer getFail() {
        return fail;
    }

    public void setFail(Integer fail) {
        this.fail = fail;
    }
}
