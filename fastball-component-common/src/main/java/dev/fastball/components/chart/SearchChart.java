package dev.fastball.components.chart;

import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.component.Component;

import java.util.Collection;

/**
 * @author gr@fastball.dev
 * @since 2023/1/29
 */
public interface SearchChart<T, S> extends Component {

    /**
     * 图表组件获取数据的接口
     *
     * @param search 用于搜索的条件
     * @return 返回的数据
     */
    @UIApi
    Collection<T> loadData(S search);
}
