package dev.fastball.core.annotation;

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
    String value() default "";

    boolean refresh() default true;

    boolean closePopupOnSuccess() default true;
}
