package dev.fastball.core.annotation;

import dev.fastball.core.component.Component;
import dev.fastball.core.info.action.DrawerPlacementType;
import dev.fastball.core.info.action.PopupType;

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

    String popupTitle() default "";

    PopupType popupType() default PopupType.Drawer;

    /**
     * 当 popupType = Drawer 时, 可以通过该方法制定抽屉的弹出方向
     *
     * @return 抽屉的方向
     */
    DrawerPlacementType drawerPlacementType() default DrawerPlacementType.Right;

    /**
     * 按钮打开的组件
     *
     * @return 对应组件的 Class
     */
    Class<? extends Component> component();
}
