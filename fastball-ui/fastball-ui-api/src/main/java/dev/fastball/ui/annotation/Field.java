package dev.fastball.ui.annotation;

import dev.fastball.ui.FieldType;

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

    boolean display() default true;

    FieldType type() default FieldType.AUTO;
}
