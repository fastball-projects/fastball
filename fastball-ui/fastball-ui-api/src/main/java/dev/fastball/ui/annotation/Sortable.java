package dev.fastball.ui.annotation;

import java.lang.annotation.*;

/**
 * @author gengrong
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sortable {
}
