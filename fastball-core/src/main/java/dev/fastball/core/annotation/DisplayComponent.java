package dev.fastball.core.annotation;

import dev.fastball.core.component.Component;

import java.lang.annotation.*;

/**
 * 只读模式下引用的组件
 *
 * @author gr@fastball.dev
 * @since 2023/1/11
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DisplayComponent {
    /**
     * 只读模式下引用的组件
     *
     * @return 对应组件的 Class
     */
    Class<? extends Component> value();

    /**
     * @return 当前字段值传给组件时的 props key
     */
    String valueKey() default "value";

    /**
     * @return 当前记录值传给组件时的 props key
     */
    String recordKey() default "record";
}
