package dev.fastball.core.annotation;

import dev.fastball.core.component.RecordActionFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 * @since 2022/12/16
 */
@UIApi
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordAction {

    /**
     * 按钮显示文本
     *
     * @return 按钮的显示文本
     */
    String name();

    /**
     * 按钮的 Key, 可用作数据权限或按钮过滤等场景
     * 默认为对应的 Method Name
     *
     * @return 按钮的 Key, 需组件内唯一
     */
    String key() default "";

    boolean refresh() default true;

    boolean closePopupOnSuccess() default true;

    Class<? extends RecordActionFilter> recordActionFilter() default RecordActionFilter.class;
}
