package io.test;

import dev.fastball.ui.components.tree.TreeNode;
import lombok.Data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author gr@fastball.dev
 * @since 2022/12/10
 */
@Data
public class User implements TreeNode {

    private Long id;

    private String name;

    private int age;

    private Date createdAt;

    private List<User> children;

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getKey() {
        return String.valueOf(id);
    }

    @Override
    public Collection<User> getNodeChildren() {
        return children;
    }
}
