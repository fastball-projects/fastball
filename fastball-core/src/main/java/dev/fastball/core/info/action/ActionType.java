package dev.fastball.core.info.action;

/**
 * @author gr@fastball.dev
 * @since 2023/01/08
 */
public enum ActionType {
    /**
     * 调用 Rest API
     */
    Rest,
    /**
     * 调用标准 API
     */
    API,
    /**
     * 弹窗
     */
    Popup,
    /**
     * 跳转页面
     */
    Digit,
    /**
     * 打印
     */
    Print
}