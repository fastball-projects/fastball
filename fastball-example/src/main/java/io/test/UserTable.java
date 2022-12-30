package io.test;

import dev.fastball.core.annotation.UIComponent;
import dev.fastball.ui.annotation.Button;
import dev.fastball.ui.components.form.PopupForm;
import dev.fastball.ui.components.table.Table;
import dev.fastball.ui.components.table.TableDataResult;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author gr@fastball.dev
 * @since 2022/12/10
 */
@UIComponent("UserTable")
@Component
@Table.Config(
        rowExpandedComponent = UserForm.class,
        buttons = @Button(value = "新建", component = UserPopupForm.class),
        recordButtons = @Button(value = "修改", component = UserPopupForm.class)
)
public class UserTable implements Table<User, UserQuerier> {

    static int age = 17;

    @Override
    public TableDataResult<User> loadData(UserQuerier querier) {
        User user = new User();
        user.setId(1L);
        user.setName(querier.getName() + "abc");
        user.setAge(age);
        user.setCreatedAt(new Date());
        User user2 = new User();
        user2.setId(2L);
        user2.setName(querier.getName() + "abc222");
        user2.setAge(age + 1);
        user2.setCreatedAt(new Date());
        return TableDataResult.<User>builder().data(Arrays.asList(user, user2)).build();
    }
}
