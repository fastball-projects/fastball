package dev.fastball.components.form.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldDependencies {
    FieldDependency[] value();

    ConditionComposeType composeType() default ConditionComposeType.And;

    FieldDependencyType type() default FieldDependencyType.Hidden;
}
