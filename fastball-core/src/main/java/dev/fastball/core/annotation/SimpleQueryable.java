package dev.fastball.core.annotation;

import java.lang.annotation.*;

/**
 * @author Xyf
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleQueryable {

    @Documented
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Ignore {

    }
}
