package dev.fastball.ui.components.tree;

import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.component.Component;

/**
 * @author gr@fastball.dev
 * @since 2022/12/20
 */
public interface Tree<T extends TreeNode> extends Component {

    @UIApi
    TreeDataResult<T> loadData(T parent);
}
