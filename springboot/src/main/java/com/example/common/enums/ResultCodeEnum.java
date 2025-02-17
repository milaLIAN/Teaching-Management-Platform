package com.example.common.enums;

public enum ResultCodeEnum {
    SUCCESS("200", "成功"),

    PARAM_ERROR("400", "参数异常"),
    TOKEN_INVALID_ERROR("401", "无效的token"),
    TOKEN_CHECK_ERROR("401", "token验证失败，请重新登录"),
    PARAM_LOST_ERROR("4001", "参数缺失"),

    SYSTEM_ERROR("500", "系统异常"),
    USER_EXIST_ERROR("5001", "用户名已存在"),
    USER_NOT_LOGIN("5002", "用户未登录"),
    USER_ACCOUNT_ERROR("5003", "账号或密码错误"),
    USER_NOT_EXIST_ERROR("5004", "用户不存在"),
    PARAM_PASSWORD_ERROR("5005", "原密码输入错误"),


    COURSE_NUM_ERROR("5006", "该课程选课人数已满，请重新选择"),
    SCORE_ALREADY_ERROR("5007", "您已录入过改学生的本课程成绩"),

    COMMENT_ALREADY_ERROR("5008","你已经对该门课进行评教，请勿重复操作"),
    ATTENDANCE_ALREADY_ERROR("5009","已存在考勤记录，请勿重复操作！"),
    STUDENT_ROLE_ERROR("5010","仅学生用户可以访问此接口"),
    TEACHER_ROLE_ERROR("5011","仅教师查看")
    ;

    public String code;
    public String msg;

    ResultCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
