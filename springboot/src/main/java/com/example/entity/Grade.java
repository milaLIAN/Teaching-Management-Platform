package com.example.entity;

public class Grade {

    private static final long serialVersionUID = 1L;

    /** ID */
    private Integer id;
    private String name; //年级名称

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
}
