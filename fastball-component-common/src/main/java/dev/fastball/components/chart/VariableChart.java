package dev.fastball.components.chart;

import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.component.Component;

import java.util.Collection;

/**
 * @author gr@fastball.dev
 * @since 2023/1/29
 */
public interface VariableChart<T, P> extends Component {

    /**
     * 图表组件获取数据的接口
     *
     * @param param 传入该组件的入参, 可用于组件间联动传递
     * @return 返回的数据
     */
    @UIApi
    Collection<T> loadData(P param);
}
