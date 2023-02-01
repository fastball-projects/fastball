package dev.fastball.core.annotation;

import java.lang.annotation.*;

/**
 * @author gr@fastball.dev
 * @since 2022/12/10
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UIApi {

    boolean needRecordFilter() default false;
}
