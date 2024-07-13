package com.example.entity;

public class StudentGpaDto {
    private String studentName;
    private Double gpa;

    // 构造函数
    public StudentGpaDto(String studentName, Double gpa) {
        this.studentName = studentName;
        this.gpa = gpa;
    }

    // Getter 和 Setter 方法
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }
}
