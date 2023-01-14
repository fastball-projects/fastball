package dev.fastball.core.annotation;

import dev.fastball.core.component.Component;
import dev.fastball.core.info.basic.ViewActionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 * @since 2022/12/17
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewAction {
    /**
     * 按钮显示文本
     *
     * @return 按钮的显示文本
     */
    String value();

    /**
     * 按钮点击时, 触发的视图动作的类型, 目前支持支 Popup
     *
     * @return 视图动作的类型
     */
    ViewActionType type() default ViewActionType.Popup;

    /**
     * 当 type=Popup 时配置
     *
     * @return 弹出窗口的相关配置
     */
    Popup popup() default @Popup(component = Component.class);

    /**
     * 当 type=Menu 时配置
     *
     * @return 跳转菜单的相关配置
     */
    GotoMenu gotoMenu() default @GotoMenu;

    /**
     * 当 type=Link 时配置
     *
     * @return 跳转链接的相关配置
     */
    GotoLink gotoLink() default @GotoLink;
}
