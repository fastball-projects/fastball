package dev.fastball.components.timeline;

import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.component.Component;

import java.util.Collection;

/**
 * @author gr@fastball.dev
 * @since 2023/1/9
 */
public interface Timeline<T> extends Component {
    /**
     * 时间轴组件获取数据的接口
     *
     * @return 返回的数据
     */
    @UIApi
    Collection<T> loadData();
}
