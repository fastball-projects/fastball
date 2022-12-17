package dev.fastball.ui.components.table;

import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.component.Component;
import dev.fastball.ui.annotation.Button;

import java.lang.annotation.*;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public interface Table<T, Q> extends Component {

    /**
     * 列表获取数据的接口
     *
     * @param querier 用于查询的条件
     * @return 返回的数据
     */
    @UIApi
    TableDataResult<T> loadData(Q querier);

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Config {
        String title() default "";

        Class<? extends Component> rowExpandedComponent() default Component.class;

        Button[] buttons() default {};

        Button[] recordButtons() default {};
    }

    /**
     * 当 Action 执行完毕之后, 执行列表的数据重新加载
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Reload {
    }

    @Documented
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Sortable {
    }

}
