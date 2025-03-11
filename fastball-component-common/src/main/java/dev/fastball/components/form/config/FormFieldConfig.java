package dev.fastball.components.form.config;

import dev.fastball.meta.basic.DisplayType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface FormFieldConfig {

    String[] field();

    String title() default "";

    int order() default 0;

    boolean readonly() default false;

    DisplayType display() default DisplayType.Show;
}
