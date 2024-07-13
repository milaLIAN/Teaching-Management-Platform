package com.example.entity;

/**
 * 学院信息表实体
 */
public class College {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Integer id;
    private String name; //学院名称
    private String content; //学院介绍

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
}
