package dev.fastball.components.common.config;

import dev.fastball.core.annotation.UIApi;

import java.lang.annotation.*;

/**
 * @author gr@fastball.dev
 * @since 2024/5/9
 */
@UIApi
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelectionAction {
    /**
     * 按钮显示文本
     *
     * @return 按钮的显示文本
     */
    String name();

    /**
     * 按钮的 Key, 可用作数据权限或按钮过滤等场景
     * 默认为对应的 Method Name
     *
     * @return 按钮的 Key, 需组件内唯一
     */
    String key() default "";

    /**
     * 当操作需要二次确认时, 可以配置二次确认的消息, 会在界面上有二次确认框出现
     *
     * @return 二次确认消息
     */
    String confirmMessage() default "";

    int order() default 1;

    boolean refresh() default true;

    boolean closePopupOnSuccess() default true;
}
