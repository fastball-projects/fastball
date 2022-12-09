package dev.fastball.ui.annotation;

import dev.fastball.ui.LookupAction;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lookup {
    Class<? extends LookupAction<?, ?>> value();
}
