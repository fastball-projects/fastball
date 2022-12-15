package dev.fastball.core.annotation;

import java.lang.annotation.*;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UIComponent {

    String value() default "";
}
