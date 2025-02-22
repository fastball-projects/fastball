package dev.fastball.core.annotation;

import dev.fastball.meta.basic.PlacementType;
import dev.fastball.meta.basic.PopupTriggerType;
import dev.fastball.meta.basic.PopupType;

import java.lang.annotation.*;

/**
 * @author gr@fastball.dev
 * @since 2023/1/10
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Popup {

    /**
     * 按钮打开的组件
     *
     * @return 对应组件的引用配置
     */
    RefComponent value();

    /**
     * 弹出窗口的宽度, 等于 0 则为自适应, 默认为 0
     *
     * @return 弹出窗口的宽度
     */
    String width() default "45%";

    String title() default "";

    PopupType popupType() default PopupType.Drawer;

    /**
     * 弹出的触发方式, 支持点击, 悬停和右键点击
     *
     * @return Popup 的触发方式
     */
    PopupTriggerType triggerType() default PopupTriggerType.Click;

    /**
     * 当 popupType = Drawer 或 Popover 时, 可以通过该方法指定弹出方向
     *
     * @return 弹出的方向
     */
    PlacementType placementType() default PlacementType.Right;

    /**
     * 当该窗口关闭时, 是否刷新触发组件
     */
    boolean refresh() default true;

    /**
     * 当该窗口关闭时, 是否关闭触发的父级 Popup
     */
    boolean closePopupOnSuccess() default true;
}
