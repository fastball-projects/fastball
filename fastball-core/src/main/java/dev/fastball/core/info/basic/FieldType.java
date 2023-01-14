package dev.fastball.core.info.basic;

/**
 * @author gr@fastball.dev
 * @since 2023/1/15
 */
public enum FieldType {

    /**
     * 自动推断
     */
    AUTO("auto"),
    /**
     * 弹出
     */
    POPUP("popup"),

    /**
     * 引用组件
     */
    COMPONENT("component");


    FieldType(String type) {
        this.type = type;
    }

    private final String type;

    public String getType() {
        return type;
    }
}
