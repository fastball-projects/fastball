package dev.fastball.components.form;

import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.component.Component;

/**
 * @author gr@fastball.dev
 * @since 2022/12/14
 */
public interface VariableForm<T, P> extends Component {
    /**
     * 获取表单初始化数据的接口
     *
     * @param param 传入该组件的入参, 可用于数据初始化, 也可用于组件间联动传递
     * @return 表单的初始化数据
     */
    @UIApi
    T loadData(P param);
}
