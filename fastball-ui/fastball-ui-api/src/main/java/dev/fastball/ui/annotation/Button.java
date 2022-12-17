package dev.fastball.ui.annotation;

import dev.fastball.core.component.Component;
import dev.fastball.core.component.PopupComponent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 * @since 2022/12/17
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Button {
    /**
     * 按钮显示文本
     *
     * @return 按钮的显示文本
     */
    String value() default "";

    /**
     * 按钮打开的组件
     *
     * @return 对应组件的 Class
     */
    Class<? extends PopupComponent> component();
}
