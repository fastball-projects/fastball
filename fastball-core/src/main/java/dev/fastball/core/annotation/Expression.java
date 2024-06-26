package dev.fastball.core.annotation;

import java.lang.annotation.*;

/**
 * @deprecated use {@link CalculatedField}
 * @author gr@fastball.dev
 * @since 2023/5/11
 */
@Documented
@Deprecated
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Expression {
    String[] fields();

    String expression();
}
