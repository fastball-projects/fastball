package dev.fastball.core.annotation;

import java.lang.annotation.*;


/**
 * @author Geng rong
 * <p>
 * 字段占位符, 当字段为空时, 会显示占位符
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldPlaceholder {
    String value();
}
