package dev.fastball.core.annotation;

import java.lang.annotation.*;

/**
 * @author gr@fastball.dev
 * @since 2023/5/11
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Expression {
    String[] fields();

    String expression();
}
