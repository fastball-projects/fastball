package dev.fastball.components.form.config;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldDependency {

    String field();

    String value() default "";

    FieldDependencyCondition condition() default FieldDependencyCondition.Equals;

    FieldDependencyType type() default FieldDependencyType.Hidden;
}
