package dev.fastball.core.annotation;


import java.lang.annotation.*;

/**
 * @author gr@fastball.dev
 * @since 2023/5/10
 */
@Documented
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface FillField {

    /**
     * @return 选项的字段 Key
     */
    String fromField();

    /**
     * @return 填充的目标字段 Key
     */
    String targetField();

    /**
     * 仅为目标值为空时, 进行填充
     */
    boolean onlyEmpty() default false;
}
