package dev.fastball.components.form.config;

import dev.fastball.core.annotation.UIApi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@UIApi
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueChangeHandler {
    String[] watchFields() default {};
}
