package dev.fastball.core.annotation;

import java.lang.annotation.*;

/**
 * @author gr@fastball.dev
 * @since 2024/1/10
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicPopupRule {

    String[] values();

    /**
     * 按钮打开的组件
     *
     * @return 对应组件的引用配置
     */
    RefComponent component();
}
