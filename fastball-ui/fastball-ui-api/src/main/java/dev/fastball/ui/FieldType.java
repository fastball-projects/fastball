package dev.fastball.ui;

public enum FieldType {
    /**
     * 自动推断
     */
    AUTO("auto"),
    /**
     * 密码输入框
     */
    PASSWORD("password"),
    /**
     * 金额输入框
     */
    MONEY("money"),
    /**
     * 文本域
     */
    TEXTAREA("textarea"),
    /**
     * 日期
     */
    DATE("date"),
    /**
     * 日期时间
     */
    DATE_TIME("dateTime"),
    /**
     * 周
     */
    DATE_WEEK("dateWeek"),
    /**
     * 月
     */
    DATE_MONTH("dateMonth"),
    /**
     * 季度输入
     */
    DATE_QUARTER("dateQuarter"),
    /**
     * 年份输入
     */
    DATE_YEAR("dateYear"),
    /**
     * 日期区间
     */
    DATE_RANGE("dateRange"),
    /**
     * 日期时间区间
     */
    DATE_TIME_RANGE("dateTimeRange"),
    /**
     * 时间
     */
    TIME("time"),
    /**
     * 时间区间
     */
    TIME_RANGE("timeRange"),
    /**
     * 文本框
     */
    TEXT("text"),
    /**
     * 下拉框
     */
    SELECT("select"),
    /**
     * 树形下拉框
     */
    TREE_SELECT("treeSelect"),
    /**
     * 多选框
     */
    CHECKBOX("checkbox"),
    /**
     * 星级组件
     */
    RATE("rate"),
    /**
     * 单选框
     */
    RADIO("radio"),
    /**
     * 按钮单选框
     */
    RADIO_BUTTON("radioButton"),
    /**
     * 进度条
     */
    PROGRESS("progress"),
    /**
     * 百分比组件
     */
    PERCENT("percent"),
    /**
     * 数字输入框
     */
    DIGIT("digit"),
    /**
     * 秒格式化
     */
    SECOND("second"),
    /**
     * 头像
     */
    AVATAR("avatar"),
    /**
     * 代码框
     */
    CODE("code"),
    /**
     * 开关
     */
    SWITCH("switch"),
    /**
     * 相对于当前时间
     */
    FROM_NOW("fromNow"),

    /**
     * 图片
     */
    IMAGE("image"),

    /**
     * 代码框，但是带了 json 格式化
     */
    JSON_CODE("jsonCode"),

    /**
     * 颜色选择器
     */
    COLOR("color"),

    /**
     * 级联选择器
     */
    CASCADER("cascader");

    FieldType(String type) {
        this.type = type;
    }

    private final String type;

    public String getType() {
        return type;
    }
}
