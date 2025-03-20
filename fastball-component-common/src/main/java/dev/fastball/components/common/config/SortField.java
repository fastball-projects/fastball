package dev.fastball.components.common.config;

import dev.fastball.components.common.metadata.sort.SortFieldType;

import java.lang.annotation.*;


@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SortField {
    int order() default 0;

    SortFieldType type() default SortFieldType.BOTH;
}
