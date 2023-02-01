package dev.fastball.core.annotation;

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
     * 展示模式下引用的组件
     *
     * @return 对应组件的引用
     */
    RefComponent value();
}
