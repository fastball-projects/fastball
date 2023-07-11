package dev.fastball.core.annotation;

import dev.fastball.core.component.TreeLookupAction;

import java.lang.annotation.*;

/**
 * @author gr@fastball.dev
 * @since 2023/01/08
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TreeLookup {
    Class<? extends TreeLookupAction> value();

    String labelField();

    String valueField();

    String childrenField();

    String[] selectorTableFields() default {};

    boolean showSearch() default true;

    FillField[] extraFillFields() default {};
}
