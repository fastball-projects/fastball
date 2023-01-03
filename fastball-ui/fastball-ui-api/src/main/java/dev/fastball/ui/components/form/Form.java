package dev.fastball.ui.components.form;

import dev.fastball.core.component.Component;
import dev.fastball.ui.annotation.Action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 * @since 2022/12/14
 */
public interface Form<T> extends Component {
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Config {
        boolean showReset() default true;

        Action[] buttons() default {};
    }
}
