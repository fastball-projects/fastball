package dev.fastball.core.annotation;

import dev.fastball.core.DefaultValues;

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
    String trueLabel() default DefaultValues.DEFAULT_BOOLEAN_TRUE_LABEL;

    /**
     * @return 当 Boolean 值为 True 时, 展示的颜色
     */
    String trueColor() default DefaultValues.DEFAULT_BOOLEAN_TRUE_COLOR;

    /**
     * @return 当 Boolean 值为 False 时, 展示的文本
     */
    String falseLabel() default DefaultValues.DEFAULT_BOOLEAN_FALSE_LABEL;

    /**
     * @return 当 Boolean 值为 False 时, 展示的颜色
     */
    String falseColor() default DefaultValues.DEFAULT_BOOLEAN_FALSE_COLOR;
}
