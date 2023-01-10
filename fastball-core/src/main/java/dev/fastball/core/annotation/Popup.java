package dev.fastball.core.annotation;

import dev.fastball.core.component.Component;
import dev.fastball.core.info.basic.PlacementType;
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
}
