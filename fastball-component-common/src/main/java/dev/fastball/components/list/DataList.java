package dev.fastball.components.list;

import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.component.Component;
import dev.fastball.core.component.DataResult;

/**
 * @author gr@fastball.dev
 */
public interface DataList<T> extends Component {

    /**
     * 列表获取数据的接口
     *
     * @return 返回的数据
     */
    @UIApi(needRecordFilter = true)
    DataResult<T> loadData();
}
