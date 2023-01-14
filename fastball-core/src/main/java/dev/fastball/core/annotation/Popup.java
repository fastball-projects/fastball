package dev.fastball.core.annotation;

import dev.fastball.core.component.Component;
import dev.fastball.core.info.basic.PlacementType;
import dev.fastball.core.info.basic.PopupTriggerType;
import dev.fastball.core.info.basic.PopupType;

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
     * @return 对应组件的 Class
     */
    Class<? extends Component> component();

    /**
     * 调用 popup component 时传入的参数路径, 默认是当前记录
     *
     * @return 参数路径
     */
    String[] dataPath() default {};

    int width() default 720;

    String popupTitle() default "";

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
