package dev.fastball.core.annotation;


import dev.fastball.core.info.basic.DisplayType;
import dev.fastball.core.info.basic.ValueType;

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

    String placeholder() default "";

    String defaultValue() default "";

    boolean readonly() default false;

    boolean entireRow() default false;

    DisplayType display() default DisplayType.Show;

    ValueType type() default ValueType.AUTO;
}
