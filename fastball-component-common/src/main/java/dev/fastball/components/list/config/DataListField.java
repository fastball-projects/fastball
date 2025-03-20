package dev.fastball.components.list.config;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataListField {


    DataListFieldPosition position() default DataListFieldPosition.Content;

    /**
     * 是否显示字段名称, 如果不使用该注解, 默认为显示
     * 当使用该注解, 则根据该字段的注解, 显示或隐藏字段名称
     *
     * @return 是否显示字段名称
     */
    boolean showLabel() default false;


    /**
     * @return 如果为空是否显示, 默认为 true
     */
    boolean showIfNull() default true;

    /**
     * @return 为 null 时的显示内容, 默认为 "-"
     */
    String nullText() default "-";
}
