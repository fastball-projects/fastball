package io.test;

import dev.fastball.core.annotation.UIComponent;
import dev.fastball.ui.annotation.Action;
import dev.fastball.ui.annotation.RecordAction;
import dev.fastball.ui.components.tree.Tree;
import dev.fastball.ui.components.tree.TreeDataResult;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;

/**
 * @author gr@fastball.dev
 * @since 2022/12/10
 */
@UIComponent("UserTree")
@Component
@Tree.Config(
        titleField = "name",
        recordActions = @Action(value = "修改", component = UserForm.class)
)
public class UserTree implements Tree<User> {

    static int age = 17;

    @Override
    public TreeDataResult<User> loadData(User parent) {
        User user = new User();
        user.setId(123L);
        user.setName("abc");
        user.setAge(age);
        user.setCreatedAt(new Date());
        User user2 = new User();
        user.setId(124L);
        user2.setName("abc222");
        user2.setAge(age + 1);
        user2.setCreatedAt(new Date());
        user.setChildren(Collections.singletonList(user2));
        return TreeDataResult.<User>builder().data(Collections.singleton(user)).build();
    }

    @RecordAction("长大")
    public void ageAdd(User user) {
        UserTable.age = user.getAge() + 1;
    }
}
