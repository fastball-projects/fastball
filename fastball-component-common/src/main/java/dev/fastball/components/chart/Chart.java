package dev.fastball.components.chart;

import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.component.Component;

import java.util.Collection;

/**
 * @author gr@fastball.dev
 * @since 2023/1/29
 */
public interface Chart<T> extends Component {

    /**
     * 图表组件获取数据的接口
     *
     * @return 返回的数据
     */
    @UIApi
    Collection<T> loadData();
}
