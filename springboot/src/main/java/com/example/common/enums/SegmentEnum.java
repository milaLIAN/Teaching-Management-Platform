package com.example.common.enums;

public enum SegmentEnum {
    FIRST("第一大节（08:00 ~ 09:40）"),
    SECOND("第二大节（10:10 ~ 11:50）"),
    THIRD("第三大节（14:20 ~ 16:00）"),
    FORTH("第四大节（16:30 ~ 18:10）"),
    FIFTH("第五大节（19:00 ~ 20:40）"),
    ;
    public String segment;

    SegmentEnum(String segment) {
        this.segment = segment;
    }
}
