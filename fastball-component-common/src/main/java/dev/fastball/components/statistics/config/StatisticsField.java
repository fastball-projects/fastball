package dev.fastball.components.statistics.config;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StatisticsField {
    String title();

    int precision() default 2;

    String color() default "";

    String prefix() default "";

    String suffix() default "";
}
