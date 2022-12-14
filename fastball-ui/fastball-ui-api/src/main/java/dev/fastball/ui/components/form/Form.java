package dev.fastball.ui.components.form;

import dev.fastball.core.annotation.UIApi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 * @since 2022/12/14
 */
public interface Form<T> {


    @UIApi
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface RecordAction {
        /**
         * 按钮显示文本
         *
         * @return 按钮的显示文本
         */
        String value() default "";
    }
}
