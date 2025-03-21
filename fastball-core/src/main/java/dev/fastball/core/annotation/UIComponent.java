package dev.fastball.core.annotation;

import java.lang.annotation.*;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UIComponent {

    /**
     * @return 组件发起请求时的Key
     */
    String value() default "";

    /**
     * @return 生成的组件文件时的路径
     */
    String path() default "";

    /**
     * @return 组件的标题
     */
    String title() default "";
}
