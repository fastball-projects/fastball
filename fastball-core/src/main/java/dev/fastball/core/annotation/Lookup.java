package dev.fastball.core.annotation;

import dev.fastball.core.component.LookupAction;

import java.lang.annotation.*;

/**
 * @author gr@fastball.dev
 * @since 2023/01/08
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lookup {
    Class<? extends LookupAction> value();

    String labelField();

    String valueField() default "id";

    FillField[] extraFillFields() default {};
}
