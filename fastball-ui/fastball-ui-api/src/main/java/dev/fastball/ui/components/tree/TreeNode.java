package dev.fastball.ui.components.tree;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

/**
 * @author gr@fastball.dev
 * @since 2022/12/20
 */
public interface TreeNode {

    @JsonProperty()
    String getTitle();

    String getKey();

    @JsonProperty("children")
    Collection<? extends TreeNode> getNodeChildren();
}
