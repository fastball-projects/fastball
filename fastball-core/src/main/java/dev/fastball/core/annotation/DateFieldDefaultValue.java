package dev.fastball.core.annotation;

import dev.fastball.meta.basic.DateDefaultValue;
import dev.fastball.meta.basic.DateOffsetUnit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateFieldDefaultValue {

    DateDefaultValue defaultValue();

    long offset() default 0;

    DateOffsetUnit offsetUnit() default DateOffsetUnit.DAYS;
}
