package dev.fastball.core.annotation;

import java.lang.annotation.*;

/**
 * @author gr@fastball.dev
 * @since 2023/1/9
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BooleanDisplay {
    /**
     * @return 当 Boolean 值为 True 时, 展示的文本
     */
    String trueLabel() default "是";

    /**
     * @return 当 Boolean 值为 False 时, 展示的文本
     */
    String falseLabel() default "否";

}
