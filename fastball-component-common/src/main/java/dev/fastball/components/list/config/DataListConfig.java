package dev.fastball.components.list.config;


import dev.fastball.core.annotation.ViewAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataListConfig {
    /**
     * 选择项操作, 可以选择多条批量操作
     *
     * @return 选择项操作
     */
    ViewAction[] selectionViewActions() default {};
}
