package dev.fastball.ui.components.layout;

import dev.fastball.core.component.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 * @since 2022/12/19
 */
public interface LayoutComponent extends Component {
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface LeftAndRight {
        Class<? extends Component> left();

        Class<? extends Component> right();
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface TopAndBottom {
        Class<? extends Component> top();

        Class<? extends Component> bottom();
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface LeftAndTopBottom {
        Class<? extends Component> left();

        Class<? extends Component> top();

        Class<? extends Component> bottom();
    }
}
