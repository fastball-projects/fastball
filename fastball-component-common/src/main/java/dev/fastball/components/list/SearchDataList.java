package dev.fastball.components.list;

import dev.fastball.components.common.metadata.query.TableSearchParam;
import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.component.Component;
import dev.fastball.core.component.DataResult;

/**
 * @author gr@fastball.dev
 */
public interface SearchDataList<T, S> extends Component {

    /**
     * 列表获取数据的接口
     *
     * @param search 用于搜索的条件
     * @return 返回的数据
     */
    @UIApi(needRecordFilter = true)
    DataResult<T> loadData(TableSearchParam<S> search);
}
