package dev.fastball.ui.components.table;

import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.component.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

        Button[] buttons() default {};
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Button {
        /**
         * 按钮显示文本
         *
         * @return 按钮的显示文本
         */
        String value() default "";
    }


    @UIApi
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface RecordAction {
        /**
         * 按钮显示文本
         *
         * @return 按钮的显示文本
         */
        String value() default "";

        boolean refresh() default false;
    }
}
