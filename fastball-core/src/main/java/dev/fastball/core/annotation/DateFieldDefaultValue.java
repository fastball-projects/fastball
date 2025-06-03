package dev.fastball.core.annotation;

import dev.fastball.meta.basic.DateDefaultValue;
import dev.fastball.meta.basic.DateOffsetUnit;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateFieldDefaultValue {

    DateDefaultValue defaultValue();

    long offset() default 0;

    DateOffsetUnit offsetUnit() default DateOffsetUnit.DAYS;
}
