package dev.fastball.ui.components.tree;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Data
@Builder
public class TreeDataResult<T extends TreeNode> {

    private Integer total;
    private Collection<T> data;
}
