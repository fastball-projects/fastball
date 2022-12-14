package dev.fastball.core.annotation;

import dev.fastball.core.component.Component;
import dev.fastball.core.info.basic.PlacementType;
import dev.fastball.core.info.basic.PopupType;

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
public @interface Action {
    /**
     * 按钮显示文本
     *
     * @return 按钮的显示文本
     */
    String value();

    /**
     * 按钮打开的组件
     *
     * @return 对应组件的 Class
     */
    Class<? extends Component> component();

    int width() default 720;

    String popupTitle() default "";

    PopupType popupType() default PopupType.Drawer;

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
