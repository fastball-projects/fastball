package dev.fastball.components.statistics;

import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.component.Component;

/**
 * 有参数的统计组件
 *
 * @param <T> 返回的数据类型
 * @param <P>
 */
public interface VariableStatistics<T, P> extends Component {

    /**
     * 图表组件获取数据的接口
     *
     * @param param 传入该组件的入参, 可用于组件间联动传递
     * @return 返回的数据
     */
    @UIApi
    T loadData(P param);
}
