package dev.fastball.core.annotation;

import java.lang.annotation.*;

/**
 * @author gr@fastball.dev
 * @since 2023/01/08
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LookupComponent {

    String value() default "";
}
