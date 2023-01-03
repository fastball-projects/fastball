package dev.fastball.ui.components.tree;

import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.component.Component;
import dev.fastball.ui.annotation.Action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 * @since 2022/12/20
 */
public interface Tree<T> extends Component {

    @UIApi
    TreeDataResult<T> loadData(T parent);

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Config {

        String keyField() default "id";

        String titleField() default "title";

        String childrenField() default "children";

        Action[] recordActions() default {};
    }
}
