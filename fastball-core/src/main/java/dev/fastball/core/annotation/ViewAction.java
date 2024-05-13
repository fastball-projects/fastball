package dev.fastball.core.annotation;

import dev.fastball.core.component.Component;
import dev.fastball.core.component.RecordActionFilter;
import dev.fastball.meta.basic.ViewActionType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 * @since 2022/12/17
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewAction {
    /**
     * 按钮的 Key, 可用作数据权限或按钮过滤等场景
     *
     * @return 按钮的 Key, 需组件内唯一
     */
    String key();

    /**
     * 按钮显示文本
     *
     * @return 按钮的显示文本
     */
    String name();

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
    Popup popup() default @Popup(@RefComponent(Component.class));

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

    /**
     * 当 type=Print 时配置
     *
     * @return 打印动作的相关配置
     */
    Print print() default @Print(@RefComponent(Component.class));

    int order() default 0;

    boolean refresh() default true;

    boolean closePopupOnSuccess() default true;

    Class<? extends RecordActionFilter> recordActionFilter() default RecordActionFilter.class;
}
