package com.example.entity;

public class GpaAndRank {
    private Double gpa;
    private Integer rankInSpeciality;

    public GpaAndRank(Double gpa, Integer rankInSpeciality) {
        this.gpa = gpa;
        this.rankInSpeciality = rankInSpeciality;
    }

    // Getter 和 Setter 方法
    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

    public Integer getRankInSpeciality() {
        return rankInSpeciality;
    }

    public void setRankInSpeciality(Integer rankInSpeciality) {
        this.rankInSpeciality = rankInSpeciality;
    }
}
