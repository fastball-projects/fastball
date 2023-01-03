package dev.fastball.ui.components.form;

import dev.fastball.core.component.PopupComponent;
import dev.fastball.ui.PopupType;
import dev.fastball.ui.annotation.Action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 * @since 2022/12/14
 */
public interface PopupForm<T> extends PopupComponent {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Config {

        PopupType popupType() default PopupType.Drawer;

        Action[] buttons() default {};
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface CloseOnSuccess {
    }
}
