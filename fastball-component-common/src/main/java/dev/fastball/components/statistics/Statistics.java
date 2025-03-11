package dev.fastball.components.statistics;

import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.component.Component;


/**
 * @author gr@fastball.dev
 * @param <T> 统计组件的数据类型
 */
public interface Statistics<T> extends Component {

    /**
     * 统计组件获取数据的接口
     *
     * @return 返回的数据
     */
    @UIApi
    T loadData();
}
