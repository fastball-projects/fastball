package dev.fastball.core.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LookupSelector {

    String[] selectorTableFields() default {};
}
