package com.example.entity;

public class ScoreDistribution {
    private static final long serialVersionUID = 1L;
    private int excellent; // 优秀 (90分以上)
    private int good;      // 良好 (80-90分)
    private int average;   // 中等 (70-80分)
    private int pass;      // 及格 (60-70分)
    private int fail;      // 不及格 (60分以下)

    // Getters and Setters
    public int getExcellent() {
        return excellent;
    }

    public void setExcellent(int excellent) {
        this.excellent = excellent;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public int getAverage() {
        return average;
    }

    public void setAverage(int average) {
        this.average = average;
    }

    public int getPass() {
        return pass;
    }

    public void setPass(int pass) {
        this.pass = pass;
    }

    public int getFail() {
        return fail;
    }

    public void setFail(int fail) {
        this.fail = fail;
    }
}
