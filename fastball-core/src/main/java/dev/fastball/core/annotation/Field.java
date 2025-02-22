package dev.fastball.core.annotation;


import dev.fastball.meta.basic.DisplayType;
import dev.fastball.meta.basic.ValueType;

import java.lang.annotation.*;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {

    String title() default "";

    String tips() default "";

    String defaultValue() default "";

    int order() default 0;

    boolean readonly() default false;

    @Deprecated
    boolean entireRow() default false;

    @Deprecated
    String placeholder() default "";

    DisplayType display() default DisplayType.Show;

    ValueType type() default ValueType.AUTO;
}
